package ru.romanov.walletservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.romanov.walletservice.exception.InsufficientFundsException;
import ru.romanov.walletservice.exception.WalletNotFoundException;
import ru.romanov.walletservice.model.Wallet;
import ru.romanov.walletservice.model.enums.OperationType;
import ru.romanov.walletservice.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository repository;

    public Mono<Wallet> createWallet() {
        return repository.save(new Wallet());
    }

    public Mono<Wallet> performOperation(UUID walletId, OperationType operationType, BigDecimal amount) {
        return getWalletById(walletId).flatMap(w -> switch (operationType) {
            case WITHDRAW -> repository.withdraw(walletId, amount)
                    .filter(rows -> rows > 0)
                    .switchIfEmpty(Mono.error(new InsufficientFundsException("Insufficient funds")))
                    .then(getWalletById(walletId));
            case DEPOSIT -> repository.deposit(walletId, amount).then(getWalletById(walletId));
        });
    }

    public Mono<Wallet> getWalletById(UUID id) {
        return repository.findById(id).switchIfEmpty(Mono.error(new WalletNotFoundException("Wallet not found")));
    }

    public Flux<Wallet> getAllWallets() {
        return repository.findAll();
    }

    public Mono<Void> massDeleteWallet(List<UUID> ids) {
        return repository.deleteAllById(ids);
    }
}
