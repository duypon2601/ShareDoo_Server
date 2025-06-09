package com.server.ShareDoo.repository;


import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
import com.server.ShareDoo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
    Page<Product> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.productId = :id")
    Optional<Product> findActiveById(Long id);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL " +
           "AND (:keyword IS NULL OR p.name LIKE %:keyword% OR p.description LIKE %:keyword%) " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:minPrice IS NULL OR p.pricePerDay >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.pricePerDay <= :maxPrice)")
    Page<Product> searchProducts(String keyword, ProductDTO.Category category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}