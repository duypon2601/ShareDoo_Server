package com.server.ShareDoo.dto.request.productRequest;

import com.server.ShareDoo.enums.Category; // Import enum từ package enums
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDTO {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private Category category; // Sử dụng enum từ package enums

    @NotNull(message = "Price per day is required")
    @DecimalMin(value = "0.01", message = "Price per day must be greater than 0")
    private BigDecimal pricePerDay;

    @NotNull(message = "Availability status is required")
    private AvailabilityStatus availabilityStatus;

    public enum AvailabilityStatus {
        AVAILABLE, UNAVAILABLE
    }
}
