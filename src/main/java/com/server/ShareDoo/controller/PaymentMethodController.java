package com.server.ShareDoo.controller;

import com.server.ShareDoo.entity.PaymentMethod;
import com.server.ShareDoo.repository.PaymentMethodRepository;
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

    @PostMapping("/add")
    public ResponseEntity<PaymentMethod> addPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
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
}
