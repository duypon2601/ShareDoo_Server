package com.server.ShareDoo.repository;

import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    boolean existsByUser_UserIdAndProduct_ProductIdAndStatusNotAndDeletedAtIsNull(Integer userId, Long productId, String status);
    List<Rental> findByUser_UserIdAndDeletedAtIsNull(Integer userId);
    List<Rental> findTop100ByDeletedAtIsNullOrderByCreatedAtDesc();

    @Query("SELECT r.product FROM Rental r WHERE r.user.userId = :userId AND r.deletedAt IS NULL")
    List<Product> findProductsByUserId(@Param("userId") Long userId);

    List<Rental> findByUser_UserId(Integer userId);

    List<Rental> findByProduct_ProductId(Long productId);

    List<Rental> findByUser_UserIdAndProduct_ProductId(Integer userId, Long productId);

    List<Rental> findByProduct(Product product);

    @Query("SELECT r FROM Rental r WHERE r.user.userId = :userId AND r.deletedAt IS NULL")
    List<Rental> findActiveByUserId(@Param("userId") Integer userId);
}