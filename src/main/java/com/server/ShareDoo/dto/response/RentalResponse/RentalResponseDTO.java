package com.server.ShareDoo.dto.response.RentalResponse;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RentalResponseDTO {
    private Long orderCode;
    private Long id;
    private Long userId;
    private Long productId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double totalPrice;
    private String status;
    private String paymentUrl;
}