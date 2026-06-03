package br.com.dbserver.banco_digital.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.dbserver.banco_digital.dto.conta.ContaRequest;
import br.com.dbserver.banco_digital.dto.conta.ContaResponse;
import br.com.dbserver.banco_digital.dto.transacao.TransacaoRequest;
import br.com.dbserver.banco_digital.dto.transacao.TransacaoResponse;
import br.com.dbserver.banco_digital.exception.especies.SaldoInsuficienteException;
import br.com.dbserver.banco_digital.models.Conta;
import br.com.dbserver.banco_digital.models.Transacao;
import br.com.dbserver.banco_digital.repository.ContaRepository;
import br.com.dbserver.banco_digital.repository.TransacaoRepository;

@ExtendWith(MockitoExtension.class)
class BancoDigitalServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private BancoDigitalService bancoDigitalService;

    private Conta contaOrigem;
    private Conta contaDestino;

    @BeforeEach
    void setUp() {
        contaOrigem = Conta.builder().id(1L).nomeTitular("Pedro").saldo(new BigDecimal("1000.00")).build();
        contaDestino = Conta.builder().id(2L).nomeTitular("Maria").saldo(new BigDecimal("1000.00")).build();
    }

    @Test
    void deveCriarContaComSucesso() {
        ContaRequest request = new ContaRequest("Pedro Felipe", new BigDecimal("500.00"));
        Conta contaSalva = Conta.builder().id(1L).nomeTitular("Pedro Felipe").saldo(new BigDecimal("500.00")).build();

        when(contaRepository.save(any(Conta.class))).thenReturn(contaSalva);

        ContaResponse response = bancoDigitalService.criarConta(request);

        assertNotNull(response);
        assertEquals("Pedro Felipe", response.nomeTitular());
        assertEquals(new BigDecimal("500.00"), response.saldo());
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    void deveRealizarTransacaoComSucesso() {
        TransacaoRequest request = new TransacaoRequest(1L, 2L, new BigDecimal("200.00"));
        Transacao transacaoSalva = Transacao.builder()
                .id(10L)
                .contaOrigem(contaOrigem)
                .contaDestino(contaDestino)
                .valor(new BigDecimal("200.00"))
                .build();

        when(contaRepository.findByIdWithLock(1L)).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findByIdWithLock(2L)).thenReturn(Optional.of(contaDestino));
        when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacaoSalva);

        TransacaoResponse response = bancoDigitalService.realizarTransacao(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("800.00"), contaOrigem.getSaldo());
        assertEquals(new BigDecimal("1200.00"), contaDestino.getSaldo());
        verify(notificacaoService, times(1)).enviarNotificacaoTransferencia(any(), any(), any());
    }

    @Test
    void deveLancarExceptionQuandoSaldoForInsuficiente() {
        TransacaoRequest request = new TransacaoRequest(1L, 2L, new BigDecimal("1500.00"));

        when(contaRepository.findByIdWithLock(1L)).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findByIdWithLock(2L)).thenReturn(Optional.of(contaDestino));

        assertThrows(SaldoInsuficienteException.class, () -> bancoDigitalService.realizarTransacao(request));
        
        verify(transacaoRepository, never()).save(any());
    }
}