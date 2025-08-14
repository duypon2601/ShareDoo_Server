package com.server.ShareDoo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequestDTO {
    private Long id;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private String userFullName;
    private String walletName;
    private String paymentMethodName;
}
