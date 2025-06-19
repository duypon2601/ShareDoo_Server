package com.server.ShareDoo.repository;

import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.enums.Category;
import com.server.ShareDoo.enums.AvailabilityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
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
    Page<Product> searchProducts(String keyword, Category category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    List<Product> findByAvailabilityStatus(AvailabilityStatus availabilityStatus);

    List<Product> findByCategory(Category category);

    List<Product> findByAvailabilityStatusAndCategory(AvailabilityStatus availabilityStatus, Category category);

    @Query("SELECT p FROM Product p WHERE p.category IN :categories AND p.availabilityStatus = 'AVAILABLE'")
    List<Product> findAvailableProductsByCategories(@Param("categories") List<Category> categories);

    @Query("SELECT p FROM Product p WHERE p.availabilityStatus = :status")
    List<Product> findAvailableProducts(@Param("status") AvailabilityStatus status);

    List<Product> findByUserId(Integer userId);

    List<Product> findByUserIdAndAvailabilityStatus(Integer userId, AvailabilityStatus availabilityStatus);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByName(@Param("keyword") String keyword);

    List<Product> findByAvailabilityStatusIn(List<AvailabilityStatus> statuses);

    Long countByAvailabilityStatus(AvailabilityStatus availabilityStatus);

    List<Product> findByDeletedAtIsNull();

    List<Product> findByCategoryAndAvailabilityStatus(Category category, AvailabilityStatus availabilityStatus);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.availabilityStatus = :status")
    List<Product> findAvailableProductsByCategory(@Param("category") Category category, @Param("status") AvailabilityStatus status);

    @Query("SELECT p FROM Product p WHERE p.availabilityStatus = :status")
    List<Product> findProductsByAvailabilityStatus(@Param("status") AvailabilityStatus status);

    @Query("SELECT p FROM Product p WHERE p.category = :category")
    List<Product> findProductsByCategory(@Param("category") Category category);

    @Query("SELECT p FROM Product p WHERE p.availabilityStatus = :status AND p.category = :category")
    List<Product> findProductsByStatusAndCategory(@Param("status") AvailabilityStatus status, @Param("category") Category category);
}
