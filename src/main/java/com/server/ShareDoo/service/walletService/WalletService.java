package com.server.ShareDoo.service.walletService;

import com.server.ShareDoo.entity.Wallet;
import com.server.ShareDoo.entity.WalletTransaction;
import com.server.ShareDoo.entity.User;
import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    Wallet getWalletByUser(User user);
    Wallet getWalletByUserId(Integer userId);
    Wallet createWalletForUser(User user);
    Wallet deposit(User user, BigDecimal amount, String description, Long orderCode);
    WalletTransaction requestWithdraw(User user, BigDecimal amount, String description, Long orderCode);
    List<WalletTransaction> getWalletTransactions(User user);
    List<WalletTransaction> getWalletTransactionsByWalletId(Long walletId);
}
