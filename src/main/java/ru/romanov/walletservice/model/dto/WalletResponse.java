package ru.romanov.walletservice.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletResponse {

    private UUID walletId;
    private BigDecimal balance;
}
