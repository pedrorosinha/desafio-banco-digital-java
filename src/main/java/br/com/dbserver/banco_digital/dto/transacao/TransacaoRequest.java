package br.com.dbserver.banco_digital.dto.transacao;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransacaoRequest(
    @NotNull(message = "O ID da conta de origem é obrigatório")
    Long contaOrigemId,

    @NotNull(message = "O ID da conta de destino é obrigatório")
    Long contaDestinoId,
    
    @NotNull(message = "O valor da transação é obrigatório")
    @Positive(message = "O valor da transação deve ser positivo")
    BigDecimal valor
) {}