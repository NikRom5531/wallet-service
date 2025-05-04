package ru.romanov.walletservice.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Table(name = "wallets")
public class Wallet {

    @Id
    private UUID id;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal balance = BigDecimal.ZERO;
}
