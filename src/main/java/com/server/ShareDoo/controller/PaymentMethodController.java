package com.server.ShareDoo.controller;

import com.server.ShareDoo.entity.PaymentMethod;
import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.repository.PaymentMethodRepository;
import com.server.ShareDoo.repository.UserRepository;

import java.security.Principal;
import java.util.Optional;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<PaymentMethod> addPaymentMethod(@RequestBody PaymentMethod paymentMethod, Principal principal) {
        // Lấy user từ username trong token
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().build();
        User user = userOpt.get();
        paymentMethod.setUserId(user.getUserId());
        // Nếu cần set thêm walletId, lấy từ user hoặc truyền từ FE
        PaymentMethod saved = paymentMethodRepository.save(paymentMethod);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentMethod>> getByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(paymentMethodRepository.findByUserId(userId));
    }

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<PaymentMethod>> getByWallet(@PathVariable Long walletId) {
        return ResponseEntity.ok(paymentMethodRepository.findByWalletId(walletId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PaymentMethod>> getMyPaymentMethods(Principal principal) {
        Optional<User> userOpt = userRepository.findByUsername(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().build();
        User user = userOpt.get();
        List<PaymentMethod> list = paymentMethodRepository.findByUserId(user.getUserId());
        return ResponseEntity.ok(list);
    }
}
