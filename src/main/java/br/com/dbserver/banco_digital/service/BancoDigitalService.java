package br.com.dbserver.banco_digital.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.dbserver.banco_digital.dto.conta.ContaRequest;
import br.com.dbserver.banco_digital.dto.conta.ContaResponse;
import br.com.dbserver.banco_digital.dto.transacao.TransacaoRequest;
import br.com.dbserver.banco_digital.dto.transacao.TransacaoResponse;
import br.com.dbserver.banco_digital.exception.especies.ContaNaoEncontradaException;
import br.com.dbserver.banco_digital.exception.especies.SaldoInsuficienteException;
import br.com.dbserver.banco_digital.models.Conta;
import br.com.dbserver.banco_digital.models.Transacao;
import br.com.dbserver.banco_digital.repository.ContaRepository;
import br.com.dbserver.banco_digital.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BancoDigitalService {

    private static final String MSG_CONTA_NAO_ENCONTRADA = "Conta não encontrada.";
    private static final String MSG_TRANSACAO_NAO_ENCONTRADA = "Transação não encontrada.";

    private final ContaRepository contaRepository;
    private final TransacaoRepository transacaoRepository;
    private final NotificacaoService notificacaoService;

    @Transactional
    public ContaResponse criarConta(ContaRequest request) {
        Conta novaConta = Conta.builder()
                .nomeTitular(request.nomeTitular())
                .saldo(request.saldoInicial())
                .build();

        Conta contaSalva = contaRepository.save(novaConta);
        return ContaResponse.fromEntity(contaSalva);
    }

    @Transactional(readOnly = true)
    public ContaResponse buscarPorId(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(MSG_CONTA_NAO_ENCONTRADA));
        return ContaResponse.fromEntity(conta);
    }

    @Transactional
    public ContaResponse atualizarConta(Long id, ContaRequest request) {
        Conta conta = contaRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(MSG_CONTA_NAO_ENCONTRADA));

        conta.setNomeTitular(request.nomeTitular());
        Conta contaAtualizada = contaRepository.save(conta);
        return ContaResponse.fromEntity(contaAtualizada);
    }

    @Transactional
    public void deletarConta(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(MSG_CONTA_NAO_ENCONTRADA));
        contaRepository.delete(conta);
    }

    @Transactional
    public TransacaoResponse realizarTransacao(TransacaoRequest request) {

        if (request.contaOrigemId().equals(request.contaDestinoId())) {
            throw new IllegalArgumentException("A conta de origem não pode ser igual à conta de destino.");
        }

        Conta origem = contaRepository.findByIdWithLock(request.contaOrigemId())
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta de origem não encontrada"));

        Conta destino = contaRepository.findByIdWithLock(request.contaDestinoId())
                .orElseThrow(() -> new ContaNaoEncontradaException("Conta de destino não encontrada"));

        if (origem.getSaldo().compareTo(request.valor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente na conta de origem.");
        }

        origem.setSaldo(origem.getSaldo().subtract(request.valor()));
        destino.setSaldo(destino.getSaldo().add(request.valor()));

        contaRepository.save(origem);
        contaRepository.save(destino);

        Transacao transacao = Transacao.builder()
                .contaOrigem(origem)
                .contaDestino(destino)
                .valor(request.valor())
                .dataHora(LocalDateTime.now())
                .build();

        Transacao transacaoSalva = transacaoRepository.save(transacao);

        notificacaoService.enviarNotificacaoTransferencia(origem, destino, request.valor());

        return TransacaoResponse.fromEntity(transacaoSalva);
    }

    @Transactional(readOnly = true)
    public TransacaoResponse buscarTransacaoPorId(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(MSG_TRANSACAO_NAO_ENCONTRADA));
        return TransacaoResponse.fromEntity(transacao);
    }

    @Transactional
    public void deletarTransacao(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(MSG_TRANSACAO_NAO_ENCONTRADA));
        transacaoRepository.delete(transacao);
    }
}