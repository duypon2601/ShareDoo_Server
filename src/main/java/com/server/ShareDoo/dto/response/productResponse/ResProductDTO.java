package com.server.ShareDoo.dto.response.productResponse;

import com.server.ShareDoo.enums.Category;
import com.server.ShareDoo.enums.AvailabilityStatus;
import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResProductDTO {
    private Long productId;
    private Integer userId;
    private String name;
    private String description;
    private Category category;
    private BigDecimal pricePerDay;
    private AvailabilityStatus availabilityStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double similarityScore;
}