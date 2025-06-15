package com.server.ShareDoo.repository;

import com.server.ShareDoo.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    boolean existsByUser_UserIdAndProduct_ProductIdAndStatusNotAndDeletedAtIsNull(Integer userId, Long productId, String status);
    List<Rental> findByUser_UserIdAndDeletedAtIsNull(Integer userId);
    List<Rental> findTop100ByDeletedAtIsNullOrderByCreatedAtDesc();
}