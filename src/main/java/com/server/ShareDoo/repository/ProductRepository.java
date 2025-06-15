package com.server.ShareDoo.repository;

import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Xóa import redundant và chỉ giữ import cần thiết
    // import com.server.ShareDoo.enums.Category;

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
    Page<Product> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.productId = :id")
    Optional<Product> findActiveById(Long id);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL " +
            "AND (:keyword IS NULL OR p.name LIKE %:keyword% OR p.description LIKE %:keyword%) " +
            "AND (:category IS NULL OR p.category = :category) " +
            "AND (:minPrice IS NULL OR p.pricePerDay >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.pricePerDay <= :maxPrice)")
    Page<Product> searchProducts(String keyword, Category category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    List<Product> findByAvailabilityStatus(ProductDTO.AvailabilityStatus availabilityStatus);

    List<Product> findByCategory(Category category);

    List<Product> findByAvailabilityStatusAndCategory(
            ProductDTO.AvailabilityStatus availabilityStatus,
            Category category);

    @Query("SELECT p FROM Product p WHERE p.category IN :categories AND p.availabilityStatus = 'AVAILABLE'")
    List<Product> findAvailableProductsByCategories(@Param("categories") List<Category> categories);

    @Query("SELECT p FROM Product p WHERE p.availabilityStatus = :status")
    List<Product> findAvailableProducts(@Param("status") ProductDTO.AvailabilityStatus status);

    List<Product> findByUserId(Integer userId);

    List<Product> findByUserIdAndAvailabilityStatus(Integer userId, ProductDTO.AvailabilityStatus availabilityStatus);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByName(@Param("keyword") String keyword);

    List<Product> findByAvailabilityStatusIn(List<ProductDTO.AvailabilityStatus> statuses);

    Long countByAvailabilityStatus(ProductDTO.AvailabilityStatus availabilityStatus);

    List<Product> findByDeletedAtIsNull();
}
