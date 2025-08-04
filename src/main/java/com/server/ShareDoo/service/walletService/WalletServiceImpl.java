package com.server.ShareDoo.service.walletService;

import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.entity.Wallet;
import com.server.ShareDoo.entity.WalletTransaction;
import com.server.ShareDoo.entity.WalletTransaction.TransactionStatus;
import com.server.ShareDoo.entity.WalletTransaction.TransactionType;
import com.server.ShareDoo.repository.WalletRepository;
import com.server.ShareDoo.repository.WalletTransactionRepository;
import com.server.ShareDoo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;

    @Override
    public Wallet getWalletByUser(User user) {
        return walletRepository.findByUser(user).orElse(null);
    }

    @Override
    public Wallet getWalletByUserId(Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(this::getWalletByUser).orElse(null);
    }

    @Override
    @Transactional
    public Wallet createWalletForUser(User user) {
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet deposit(User user, BigDecimal amount, String description, Long orderCode) {
        Wallet wallet = getWalletByUser(user);
        if (wallet == null) wallet = createWalletForUser(user);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .description(description)
                .orderCode(orderCode)
                .createdAt(LocalDateTime.now())
                .build();
        walletTransactionRepository.save(transaction);
        return wallet;
    }

    @Override
    @Transactional
    public WalletTransaction requestWithdraw(User user, BigDecimal amount, String description, Long orderCode) {
        Wallet wallet = getWalletByUser(user);
        if (wallet == null || wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Số dư không đủ hoặc ví không tồn tại");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.WITHDRAW)
                .status(TransactionStatus.PENDING)
                .description(description)
                .orderCode(orderCode)
                .createdAt(LocalDateTime.now())
                .build();
        return walletTransactionRepository.save(transaction);
    }

    @Override
    public List<WalletTransaction> getWalletTransactions(User user) {
        Wallet wallet = getWalletByUser(user);
        if (wallet == null) return List.of();
        return walletTransactionRepository.findByWallet(wallet);
    }

    @Override
    public List<WalletTransaction> getWalletTransactionsByWalletId(Long walletId) {
        return walletTransactionRepository.findByWallet_Id(walletId);
    }
}
