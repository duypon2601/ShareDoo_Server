package com.server.ShareDoo.repository;

import com.server.ShareDoo.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    boolean existsByUser_UserIdAndProduct_ProductIdAndStatusNotAndDeletedAtIsNull(Integer userId, Long productId, String status);
}