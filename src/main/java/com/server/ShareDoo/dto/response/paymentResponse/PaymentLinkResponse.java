package com.server.ShareDoo.dto.response.paymentResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkResponse {
    private String checkoutUrl;
    private String orderCode;
    private Long orderId;
    private BigDecimal amount;
    private String status;
    private String message;
    private String qrCode; // Thêm trường này
} 