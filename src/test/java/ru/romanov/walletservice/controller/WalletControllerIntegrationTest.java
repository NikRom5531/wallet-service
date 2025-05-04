package ru.romanov.walletservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.romanov.walletservice.model.Wallet;
import ru.romanov.walletservice.model.dto.ErrorResponse;
import ru.romanov.walletservice.model.dto.WalletOperationRequest;
import ru.romanov.walletservice.model.dto.WalletResponse;
import ru.romanov.walletservice.model.enums.OperationType;
import ru.romanov.walletservice.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Testcontainers
public class WalletControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WalletRepository repository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test_wallet_db")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://" + postgres.getHost() + ":" +
                                               postgres.getMappedPort(5432) + "/test_wallet_db");
        registry.add("spring.r2dbc.username", () -> "postgres");
        registry.add("spring.r2dbc.password", () -> "postgres");
        registry.add("spring.liquibase.url", () -> "jdbc:postgresql://" + postgres.getHost() + ":" +
                                                   postgres.getMappedPort(5432) + "/test_wallet_db");
        registry.add("spring.liquibase.user", () -> "postgres");
        registry.add("spring.liquibase.password", () -> "postgres");
        registry.add("spring.liquibase.enabled", () -> true);

    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testCreateWallet() {
        webTestClient.post()
                .uri("/api/v1/wallet/create")
                .exchange()
                .expectStatus().isCreated()
                .expectBody(WalletResponse.class)
                .value(response -> {
                    assertNotNull(response.getWalletId());
                    assertEquals(BigDecimal.ZERO, response.getBalance());
                });
    }

    @Test
    void testDepositFunds() {
        UUID walletId = repository.save(new Wallet()).block().getId();

        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(walletId);
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(new BigDecimal("100.00"));

        webTestClient.post()
                .uri("/api/v1/wallet")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WalletResponse.class)
                .value(response -> {
                    assertEquals(walletId, response.getWalletId());
                    assertEquals(new BigDecimal("100.00"), response.getBalance());
                });
    }

    @Test
    void testInvalidJson() {
        String invalidJson = "{ \"walletId\": \"invalid-uuid\", \"operationType\": \"WITHDRAW\", \"amount\": \"abc\" }";

        webTestClient.post()
                .uri("/api/v1/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(response -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode()));
    }

    @Test
    void testInsufficientFunds() {
        UUID walletId = repository.save(new Wallet()).block().getId();

        WalletOperationRequest request = new WalletOperationRequest();
        request.setWalletId(walletId);
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(new BigDecimal("100.00"));

        webTestClient.post()
                .uri("/api/v1/wallet")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(response -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode()));
    }

    @Test
    void testGetNonExistentWallet() {
        UUID nonExistentId = UUID.randomUUID();

        webTestClient.get()
                .uri("/api/v1/wallets/{walletId}", nonExistentId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .value(response -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode()));
    }
}
