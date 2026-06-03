package br.com.dbserver.banco_digital.dto.conta;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ContaRequest(
    @NotBlank(message = "O nome do titular é obrigatório") 
    String nomeTitular,

    @NotNull(message = "O saldo inicial é obrigatório")
    @PositiveOrZero(message = "O saldo inicial deve ser zero ou positivo")
    BigDecimal saldoInicial
) {}
