package ru.romanov.walletservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.romanov.walletservice.exception.InsufficientFundsException;
import ru.romanov.walletservice.exception.WalletNotFoundException;
import ru.romanov.walletservice.mapper.WalletMapper;
import ru.romanov.walletservice.model.Wallet;
import ru.romanov.walletservice.model.dto.WalletResponse;
import ru.romanov.walletservice.model.enums.OperationType;
import ru.romanov.walletservice.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper mapper;

    @Transactional
    public WalletResponse createWallet() {
        return mapper.map(save(new Wallet()));
    }

    @Transactional
    public Wallet save(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    @Retryable(maxAttempts = 2000, backoff = @Backoff(delay = 50, multiplier = 1.1))
    @Transactional
    public void performOperation(UUID walletId, OperationType operationType, BigDecimal amount) {
        Wallet wallet = getWalletById(walletId);

        if (operationType == OperationType.WITHDRAW && wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        if (operationType == OperationType.DEPOSIT) {
            wallet.setBalance(wallet.getBalance().add(amount));
        } else {
            wallet.setBalance(wallet.getBalance().subtract(amount));
        }

        save(wallet);
    }

    @Transactional
    public Wallet getWalletById(UUID id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
    }

    @Transactional
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }
}
