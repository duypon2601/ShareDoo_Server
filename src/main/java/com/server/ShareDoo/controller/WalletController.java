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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
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

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@SecurityRequirement(name = "api")
@Tag(name = "wallet Management", description = "Wallet management APIs")
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {
    @Autowired
    private PayOS payOS;
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

    // Tạo link thanh toán PayOS cho nạp tiền vào ví
    @PostMapping("/deposit-link")
    public ResponseEntity<String> createDepositPaymentLink(@RequestParam BigDecimal amount, @RequestParam(required = false) String description, Principal principal) {
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");
        User user = userOpt.get();
        // Sinh orderCode riêng cho giao dịch ví
        long orderCode = System.currentTimeMillis() % 1000000 + user.getUserId();
        String desc = (description != null && description.length() > 0) ? description : ("Nạp tiền ví ShareDoo");
        if (desc.length() > 25) desc = desc.substring(0, 25);
        ItemData item = ItemData.builder()
                .name("Nạp tiền ví ShareDoo")
                .price(amount.intValue())
                .quantity(1)
                .build();
        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(amount.intValue())
                .description(desc)
                .returnUrl(payosReturnUrl)
                .cancelUrl(payosCancelUrl)
                .item(item)
                .build();
        try {
            CheckoutResponseData data = payOS.createPaymentLink(paymentData);
            return ResponseEntity.ok(data.getCheckoutUrl());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi tạo link thanh toán");
        }
    }

    // Nhận webhook từ PayOS để xác nhận nạp tiền thành công
    @PostMapping("/payos-webhook")
    public ResponseEntity<String> handlePayOSWebhook(@RequestBody ObjectNode webhookBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Webhook webhook = objectMapper.treeToValue(webhookBody, Webhook.class);
            WebhookData data = payOS.verifyPaymentWebhookData(webhook);
            String status = null;
            try {
                status = (String) data.getClass().getMethod("getTransactionStatus").invoke(data);
            } catch (Exception ignore) {}
            if (status == null) {
                try {
                    status = (String) data.getClass().getMethod("getStatus").invoke(data);
                } catch (Exception ignore) {}
            }
            Long orderCode = null;
            try {
                orderCode = (Long) data.getClass().getMethod("getOrderCode").invoke(data);
            } catch (Exception ignore) {}
            Integer userId = null;
            try {
                userId = Integer.parseInt(data.getClass().getMethod("getCustomerId").invoke(data).toString());
            } catch (Exception ignore) {}
            if (orderCode != null && status != null && "PAID".equalsIgnoreCase(status)) {
                // Tìm user theo userId nếu có, hoặc map orderCode về user nếu cần
                // Ở đây giả sử orderCode chứa userId ở cuối
                if (userId == null) {
                    userId = (int) (orderCode % 1000000); // fallback nếu customerId không truyền
                }
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    walletService.deposit(user, new BigDecimal(data.getClass().getMethod("getAmount").invoke(data).toString()), "Nạp tiền qua PayOS");
                    return ResponseEntity.ok("Nạp tiền thành công vào ví");
                }
            }
            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid webhook");
        }
    }
    // Nạp tiền vào ví (nội bộ, không dùng PayOS)
    @PostMapping("/deposit")
    public ResponseEntity<WalletDTO> deposit(@RequestParam BigDecimal amount, @RequestParam(required = false) String description, Principal principal) {
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().build();
        Wallet wallet = walletService.deposit(userOpt.get(), amount, description);
        return ResponseEntity.ok(walletMapper.toDTO(wallet));
    }

    // Yêu cầu rút tiền
    @PostMapping("/withdraw")
    public ResponseEntity<WalletTransactionDTO> requestWithdraw(@RequestParam BigDecimal amount, @RequestParam(required = false) String description, Principal principal) {
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().build();
        WalletTransaction transaction = walletService.requestWithdraw(userOpt.get(), amount, description);
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
}
