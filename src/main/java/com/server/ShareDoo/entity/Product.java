package com.server.ShareDoo.entity;


import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
import com.server.ShareDoo.enums.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mapstruct.*;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Where(clause = "deleted_at IS NULL")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String name;

    private String description;


    private String imageUrl;

    private String location;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category; // Sử dụng enum từ package enums

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductDTO.AvailabilityStatus availabilityStatus;

    @Column(nullable = false)
    private BigDecimal pricePerDay;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime deletedAt;
}