package com.server.ShareDoo.controller;

import com.server.ShareDoo.dto.response.wallet.WalletDTO;
import com.server.ShareDoo.dto.response.wallet.WalletTransactionDTO;
import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.entity.Wallet;
import com.server.ShareDoo.entity.WalletTransaction;
import com.server.ShareDoo.mapper.WalletMapper;
import com.server.ShareDoo.mapper.WalletTransactionMapper;
import com.server.ShareDoo.service.walletService.WalletService;
import com.server.ShareDoo.repository.UserRepository;
import com.server.ShareDoo.repository.WalletTransactionRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.payos.PayOS;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.WebhookData;
import vn.payos.type.Webhook;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;


@SecurityRequirement(name = "api")
@Tag(name = "wallet Management", description = "Wallet management APIs")
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {
    @Autowired
    private PayOS payOS;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    @Value("${payos.return-url}")
    private String payosReturnUrl;
    @Value("${payos.cancel-url}")
    private String payosCancelUrl;
    private final WalletService walletService;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;
    private final WalletTransactionMapper walletTransactionMapper;

    // Lấy thông tin ví của user
    @GetMapping("/me")
    public ResponseEntity<WalletDTO> getMyWallet(Principal principal) {
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().build();
        Wallet wallet = walletService.getWalletByUser(userOpt.get());
        if (wallet == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(walletMapper.toDTO(wallet));
    }

    // Tạo ví cho user hiện tại nếu chưa có
    @PostMapping("/create")
    public ResponseEntity<WalletDTO> createWalletForCurrentUser(Principal principal) {
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().build();
        User user = userOpt.get();
        Wallet existing = walletService.getWalletByUser(user);
        if (existing != null) return ResponseEntity.badRequest().body(walletMapper.toDTO(existing));
        Wallet wallet = walletService.createWalletForUser(user);
        return ResponseEntity.ok(walletMapper.toDTO(wallet));
    }



    // Tạo link thanh toán PayOS dành riêng cho nạp tiền vào ví
    @PostMapping("/deposit-link-wallet")
    public ResponseEntity<String> createDepositWalletLink(@RequestParam BigDecimal amount, @RequestParam(required = false) String description, Principal principal) {
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");
        User user = userOpt.get();
        long orderCode = System.currentTimeMillis() % 1000000 + user.getUserId();
        String desc = (description != null && description.length() > 0) ? description : ("Nạp tiền ví ShareDoo");
        if (desc.length() > 25) desc = desc.substring(0, 25);
        ItemData item = ItemData.builder()
                .name("Nạp tiền ví ShareDoo")
                .price(amount.intValue())
                .quantity(1)
                .build();
        // Truyền type=wallet vào returnUrl để FE nhận diện giao dịch nạp ví
        String returnUrlWithType = payosReturnUrl + (payosReturnUrl.contains("?") ? "&" : "?") + "type=wallet";
        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(amount.intValue())
                .description(desc)
                .returnUrl(returnUrlWithType)
                .cancelUrl(payosCancelUrl)
                .item(item)
                .build();
        try {
            CheckoutResponseData data = payOS.createPaymentLink(paymentData);
            // Tạo transaction trạng thái PENDING
            Wallet wallet = walletService.getWalletByUser(user);
            if (wallet == null) wallet = walletService.createWalletForUser(user);
            WalletTransaction pendingTx = WalletTransaction.builder()
                    .wallet(wallet)
                    .amount(amount)
                    .type(WalletTransaction.TransactionType.DEPOSIT)
                    .status(WalletTransaction.TransactionStatus.PENDING)
                    .description(desc)
                    .orderCode(orderCode)
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
            walletTransactionRepository.save(pendingTx);
            return ResponseEntity.ok(data.getCheckoutUrl());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi tạo link thanh toán");
        }
    }

    // Yêu cầu rút tiền
    @PostMapping("/withdraw")
    public ResponseEntity<WalletTransactionDTO> requestWithdraw(@RequestParam BigDecimal amount, @RequestParam(required = false) String description, Principal principal) {
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().build();
        WalletTransaction transaction = walletService.requestWithdraw(userOpt.get(), amount, description, null);
        return ResponseEntity.ok(walletTransactionMapper.toDTO(transaction));
    }

    // Lấy lịch sử giao dịch ví
    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransactionDTO>> getMyWalletTransactions(Principal principal) {
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().build();
        List<WalletTransaction> transactions = walletService.getWalletTransactions(userOpt.get());
        List<WalletTransactionDTO> dtos = transactions.stream().map(walletTransactionMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Cộng tiền vào ví cho user nhận tiền dựa trên orderCode (tương tự payment-status)
    @PostMapping("/credit-by-ordercode")
    public ResponseEntity<?> creditByOrderCode(@RequestBody Map<String, Object> body) {
        try {
            final Long orderCode = body.get("orderCode") != null ? Long.valueOf(body.get("orderCode").toString()) : null;
            final String status = body.get("status") != null ? body.get("status").toString() : null;
            if (orderCode == null || status == null || !"PAID".equalsIgnoreCase(status)) {
                return ResponseEntity.badRequest().body("Thiếu orderCode hoặc trạng thái không hợp lệ");
            }
            // Lấy userId nhận tiền từ orderCode (giả sử truyền userId nhận tiền từ FE hoặc mapping được từ orderCode)
            // Tìm transaction PENDING theo orderCode
            List<WalletTransaction> txList = walletTransactionRepository.findByOrderCode(orderCode);
            WalletTransaction pendingTx = txList.stream().filter(tx -> tx.getStatus() == WalletTransaction.TransactionStatus.PENDING && tx.getType() == WalletTransaction.TransactionType.DEPOSIT).findFirst().orElse(null);
            if (pendingTx == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy giao dịch PENDING phù hợp với orderCode");
            }
            Wallet wallet = pendingTx.getWallet();
            if (wallet == null) return ResponseEntity.badRequest().body("Không tìm thấy ví để cộng tiền");
            // Tránh cộng lặp
            if (pendingTx.getStatus() == WalletTransaction.TransactionStatus.SUCCESS) {
                return ResponseEntity.ok("Giao dịch đã được cộng vào ví trước đó");
            }
            // Lấy amount từ transaction
            BigDecimal amount = pendingTx.getAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return ResponseEntity.badRequest().body("Thiếu hoặc sai amount trong transaction");
            // Cộng tiền vào ví
            wallet.setBalance(wallet.getBalance().add(amount));
            wallet.setUpdatedAt(java.time.LocalDateTime.now());
            pendingTx.setStatus(WalletTransaction.TransactionStatus.SUCCESS);
            walletTransactionRepository.save(pendingTx);
            // Lưu lại ví
            walletService.getWalletByUserId(wallet.getUser().getUserId()); // ensure wallet is up to date
            return ResponseEntity.ok("Cộng tiền vào ví thành công và cập nhật trạng thái SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi xử lý cộng tiền theo orderCode");
        }
    }


}
// Nhận webhook từ PayOS để xác nhận nạp tiền thành công
//    @PostMapping("/payos-webhook")
//    public ResponseEntity<String> handlePayOSWebhook(@RequestBody ObjectNode webhookBody) {
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            Webhook webhook = objectMapper.treeToValue(webhookBody, Webhook.class);
//            WebhookData data = payOS.verifyPaymentWebhookData(webhook);
//            String status = null;
//            try {
//                status = (String) data.getClass().getMethod("getTransactionStatus").invoke(data);
//            } catch (Exception ignore) {}
//            if (status == null) {
//                try {
//                    status = (String) data.getClass().getMethod("getStatus").invoke(data);
//                } catch (Exception ignore) {}
//            }
//            Long orderCode = null;
//            try {
//                orderCode = (Long) data.getClass().getMethod("getOrderCode").invoke(data);
//            } catch (Exception ignore) {}
//            Integer userId = null;
//            try {
//                userId = Integer.parseInt(data.getClass().getMethod("getCustomerId").invoke(data).toString());
//            } catch (Exception ignore) {}
//            if (orderCode != null && status != null && "PAID".equalsIgnoreCase(status)) {
//                // Tìm user theo userId nếu có, hoặc map orderCode về user nếu cần
//                // Ở đây giả sử orderCode chứa userId ở cuối
//                if (userId == null) {
//                    userId = (int) (orderCode % 1000000); // fallback nếu customerId không truyền
//                }
//                Optional<User> userOpt = userRepository.findById(userId);
//                if (userOpt.isPresent()) {
//                    User user = userOpt.get();
//                    walletService.deposit(user, new BigDecimal(data.getClass().getMethod("getAmount").invoke(data).toString()), "Nạp tiền qua PayOS");
//                    return ResponseEntity.ok("Nạp tiền thành công vào ví");
//                }
//            }
//            return ResponseEntity.ok("Webhook received");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body("Invalid webhook");
//        }
//    }
//    // Nạp tiền vào ví (nội bộ, không dùng PayOS)
//    @PostMapping("/deposit")
//    public ResponseEntity<WalletDTO> deposit(@RequestParam BigDecimal amount, @RequestParam(required = false) String description, Principal principal) {
//        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
//        if (userOpt.isEmpty()) return ResponseEntity.badRequest().build();
//        Wallet wallet = walletService.deposit(userOpt.get(), amount, description);
//        return ResponseEntity.ok(walletMapper.toDTO(wallet));
//    }
