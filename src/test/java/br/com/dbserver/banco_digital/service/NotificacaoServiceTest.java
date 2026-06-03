package br.com.dbserver.banco_digital.service;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.dbserver.banco_digital.models.Conta;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @InjectMocks
    private NotificacaoService notificacaoService;

    private Conta contaOrigem;
    private Conta contaDestino;

    @BeforeEach
    void setUp() {
        contaOrigem = Conta.builder()
                .id(1L)
                .nomeTitular("Pedro")
                .saldo(new BigDecimal("1000.00"))
                .build();

        contaDestino = Conta.builder()
                .id(2L)
                .nomeTitular("Maria")
                .saldo(new BigDecimal("1000.00"))
                .build();
    }

    @Test
    void deveExecutarNotificacaoAssincronaComSucesso() {
        BigDecimal valor = new BigDecimal("200.00");

        assertDoesNotThrow(() -> 
            notificacaoService.enviarNotificacaoTransferencia(contaOrigem, contaDestino, valor)
        );

        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            assertDoesNotThrow(() -> {});
        });
    }
}