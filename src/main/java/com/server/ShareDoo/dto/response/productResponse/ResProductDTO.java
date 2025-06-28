package com.server.ShareDoo.dto.response.productResponse;

import com.server.ShareDoo.enums.Category;
import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResProductDTO {
    private Long productId;
    private Long userId;
    private String name;
    private String description;
    private String imageUrl;
    private String location;
    private Category category;
    private BigDecimal pricePerDay;
    private ProductDTO.AvailabilityStatus availabilityStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double similarityScore;
}