package com.server.ShareDoo.dto.request.RentalRequest;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RentalRequestDTO {
    private Long userId;
    private Long productId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double totalPrice;
}