package ru.romanov.walletservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping("/wallet")
    public ResponseEntity<Void> postWalletOperation(@RequestBody @Valid WalletOperationRequest request) {
        service.performOperation(request.getWalletId(), request.getOperationType(), request.getAmount());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<WalletResponse> getBalance(@PathVariable UUID walletId) {
        return ResponseEntity.ok(mapper.map(service.getWalletById(walletId)));
    }

    @PostMapping("/wallet/create")
    public ResponseEntity<WalletResponse> createWallet() {
        return ResponseEntity.ok(service.createWallet());
    }

    @GetMapping("/wallets")
    public ResponseEntity<List<WalletResponse>> getAllWallets() {
        return ResponseEntity.ok(mapper.map(service.getAllWallets()));
    }

}
