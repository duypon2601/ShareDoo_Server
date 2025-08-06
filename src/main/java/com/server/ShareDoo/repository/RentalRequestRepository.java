package com.server.ShareDoo.repository;

import com.server.ShareDoo.entity.RentalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRequestRepository extends JpaRepository<RentalRequest, Long> {
    java.util.List<RentalRequest> findByUserId(Long userId);
}
