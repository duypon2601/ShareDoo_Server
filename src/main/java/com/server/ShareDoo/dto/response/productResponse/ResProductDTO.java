package com.server.ShareDoo.dto.response.productResponse;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResProductDTO {
    private Long productId;
    private Long userId;
    private String name;
    private String description;
    private Category category;
    private BigDecimal pricePerDay;
    private AvailabilityStatus availabilityStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Category {
        CAMPING, ELECTRONICS, EVENTS, SPORTS, OTHERS
    }

    public enum AvailabilityStatus {
        AVAILABLE, UNAVAILABLE
    }
}