package com.server.ShareDoo.repository;

import com.server.ShareDoo.entity.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {
    List<WithdrawalRequest> findByUser_UserId(Long userId);
    List<WithdrawalRequest> findByStatus(WithdrawalRequest.Status status);
}
