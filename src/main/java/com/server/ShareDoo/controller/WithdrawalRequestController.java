package com.server.ShareDoo.controller;

import com.server.ShareDoo.entity.WithdrawalRequest;
import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.entity.Wallet;
import com.server.ShareDoo.entity.PaymentMethod;
import com.server.ShareDoo.repository.WithdrawalRequestRepository;
import com.server.ShareDoo.repository.UserRepository;
import com.server.ShareDoo.repository.WalletRepository;
import com.server.ShareDoo.repository.PaymentMethodRepository;
import com.server.ShareDoo.repository.WalletTransactionRepository;
import com.server.ShareDoo.entity.WalletTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/withdrawals")
@RequiredArgsConstructor
public class WithdrawalRequestController {
    @Autowired
    private WithdrawalRequestRepository withdrawalRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    // User tạo lệnh rút tiền
    @PostMapping("/request")
    public ResponseEntity<?> createWithdrawalRequest(@RequestParam Long userId,
                                                     @RequestParam Long walletId,
                                                     @RequestParam Long paymentMethodId,
                                                     @RequestParam BigDecimal amount) {
        Optional<User> userOpt = userRepository.findById(userId.intValue());
        Optional<Wallet> walletOpt = walletRepository.findById(walletId);
        Optional<PaymentMethod> pmOpt = paymentMethodRepository.findById(paymentMethodId);
        if (userOpt.isEmpty() || walletOpt.isEmpty() || pmOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User, Wallet hoặc PaymentMethod không tồn tại");
        }
        if (walletOpt.get().getBalance().compareTo(amount) < 0) {
            return ResponseEntity.badRequest().body("Số dư ví không đủ");
        }
        WithdrawalRequest req = WithdrawalRequest.builder()
                .user(userOpt.get())
                .wallet(walletOpt.get())
                .paymentMethod(pmOpt.get())
                .amount(amount)
                .status(WithdrawalRequest.Status.PENDING)
                .build();
        withdrawalRequestRepository.save(req);
        return ResponseEntity.ok(req);
    }

    // Admin từ chối lệnh rút tiền
    @PostMapping("/reject")
    public ResponseEntity<?> rejectWithdrawal(@RequestParam Long requestId) {
        Optional<WithdrawalRequest> reqOpt = withdrawalRequestRepository.findById(requestId);
        if (reqOpt.isEmpty()) return ResponseEntity.badRequest().body("Không tìm thấy lệnh rút tiền");
        WithdrawalRequest req = reqOpt.get();
        if (req.getStatus() != WithdrawalRequest.Status.PENDING) {
            return ResponseEntity.badRequest().body("Chỉ có thể từ chối lệnh đang chờ xử lý");
        }
        req.setStatus(WithdrawalRequest.Status.CANCELED);
        withdrawalRequestRepository.save(req);
        return ResponseEntity.ok("Lệnh rút tiền đã bị từ chối");
    }

    // Admin duyệt lệnh rút tiền
    @PostMapping("/approve")
    public ResponseEntity<?> approveWithdrawal(@RequestParam Long requestId) {
        Optional<WithdrawalRequest> reqOpt = withdrawalRequestRepository.findById(requestId);
        if (reqOpt.isEmpty()) return ResponseEntity.badRequest().body("Không tìm thấy lệnh rút tiền");
        WithdrawalRequest req = reqOpt.get();
        if (req.getStatus() != WithdrawalRequest.Status.PENDING) {
            return ResponseEntity.badRequest().body("Chỉ có thể duyệt lệnh đang chờ xử lý");
        }
        Wallet wallet = req.getWallet();
        if (wallet.getBalance().compareTo(req.getAmount()) < 0) {
            return ResponseEntity.badRequest().body("Số dư ví không đủ");
        }
        // Trừ tiền ví
        wallet.setBalance(wallet.getBalance().subtract(req.getAmount()));
        walletRepository.save(wallet);
        // Tạo lịch sử rút tiền
        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .amount(req.getAmount())
                .type(WalletTransaction.TransactionType.WITHDRAW)
                .status(WalletTransaction.TransactionStatus.SUCCESS)
                .description("Rút tiền về tài khoản ngân hàng")
                .orderCode(null)
                .build();
        walletTransactionRepository.save(tx);
        // Cập nhật trạng thái lệnh rút
        req.setStatus(WithdrawalRequest.Status.SUCCESS);
        withdrawalRequestRepository.save(req);
        return ResponseEntity.ok("Lệnh rút tiền đã được duyệt và trừ tiền thành công");
    }
}
