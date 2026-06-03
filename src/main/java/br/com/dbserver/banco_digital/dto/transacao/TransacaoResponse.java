package br.com.dbserver.banco_digital.dto.transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.dbserver.banco_digital.models.Transacao;

public record TransacaoResponse(
    Long id,
    Long contaOrigemId,
    Long contaDestinoId,
    BigDecimal valor,
    LocalDateTime dataHora
) {
    public static TransacaoResponse fromEntity(Transacao transacao) {
        return new TransacaoResponse(
            transacao.getId(),
            transacao.getContaOrigem().getId(),
            transacao.getContaDestino().getId(),
            transacao.getValor(),
            transacao.getDataHora()
        );
    }
}