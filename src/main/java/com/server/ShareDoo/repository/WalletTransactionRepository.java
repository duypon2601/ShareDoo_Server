package com.server.ShareDoo.repository;

import com.server.ShareDoo.entity.WalletTransaction;
import com.server.ShareDoo.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWallet(Wallet wallet);
    List<WalletTransaction> findByWallet_Id(Long walletId);
    List<WalletTransaction> findByOrderCode(Long orderCode);
    List<WalletTransaction> findByOrderCodeAndType(Long orderCode, WalletTransaction.TransactionType type);
}
