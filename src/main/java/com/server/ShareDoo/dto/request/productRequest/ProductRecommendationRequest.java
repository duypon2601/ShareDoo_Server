package com.server.ShareDoo.dto.request.productRequest;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class ProductRecommendationRequest {
    @NotBlank(message = "Event description is required")
    private String eventDescription;
    private String location;
    private BigDecimal maxPricePerDay;
}
