package ru.romanov.walletservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.romanov.walletservice.mapper.WalletMapper;
import ru.romanov.walletservice.model.dto.WalletOperationRequest;
import ru.romanov.walletservice.model.dto.WalletResponse;
import ru.romanov.walletservice.service.WalletService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService service;
    private final WalletMapper mapper;

    @PostMapping("/wallet/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<WalletResponse> createWallet() {
        return service.createWallet().map(mapper::map);
    }

    @PostMapping("/wallet")
    public Mono<WalletResponse> updateWallet(@RequestBody @Valid WalletOperationRequest request) {
        return service.performOperation(request.getWalletId(), request.getOperationType(), request.getAmount())
                .map(mapper::map);
    }

    @GetMapping("/wallets/{walletId}")
    public Mono<WalletResponse> getBalance(@PathVariable UUID walletId) {
        return service.getWalletById(walletId).map(mapper::map);
    }

    @GetMapping("/wallets")
    public Mono<List<WalletResponse>> getAllWallets() {
        return service.getAllWallets()
                .map(mapper::map)
                .collectList();
    }

    @DeleteMapping("/wallets")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAllWallets(@RequestBody @Valid List<UUID> walletIds) {
        return service.massDeleteWallet(walletIds);
    }
}
