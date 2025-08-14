package com.server.ShareDoo.repository;

import com.server.ShareDoo.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByUserId(Integer userId);
    List<PaymentMethod> findByWalletId(Long walletId);
}
