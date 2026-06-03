package br.com.dbserver.banco_digital.dto.conta;

import java.math.BigDecimal;
import br.com.dbserver.banco_digital.models.Conta;

public record ContaResponse(
        Long id,
        String nomeTitular,
        BigDecimal saldo) {
    public static ContaResponse fromEntity(Conta conta) {
        return new ContaResponse(conta.getId(), conta.getNomeTitular(), conta.getSaldo());
    }
}
