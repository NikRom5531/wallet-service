package ru.romanov.walletservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.romanov.walletservice.model.Wallet;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
}
