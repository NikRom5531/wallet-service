package ru.romanov.walletservice.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.romanov.walletservice.model.enums.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletOperationRequest {

    @NotNull
    private UUID walletId;

    @NotNull
    private OperationType operationType;

    @NotNull
    @DecimalMin(value = "0.01", message = "значение должно быть больше 0.00")
    private BigDecimal amount;
}
