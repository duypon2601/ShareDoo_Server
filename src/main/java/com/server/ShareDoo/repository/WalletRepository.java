package com.server.ShareDoo.repository;

import com.server.ShareDoo.entity.Wallet;
import com.server.ShareDoo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);
    Optional<Wallet> findByUser_UserId(Integer userId);
}
