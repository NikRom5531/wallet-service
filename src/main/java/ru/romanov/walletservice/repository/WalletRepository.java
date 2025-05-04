package ru.romanov.walletservice.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.romanov.walletservice.model.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface WalletRepository extends ReactiveCrudRepository<Wallet, UUID> {

    @Modifying
    @Query("UPDATE wallets SET balance = balance + :amount WHERE id = :id")
    Mono<Integer> deposit(@Param("id") UUID id, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE wallets SET balance = balance - :amount WHERE id = :id AND balance >= :amount")
    Mono<Integer> withdraw(@Param("id") UUID id, @Param("amount") BigDecimal amount);
}
