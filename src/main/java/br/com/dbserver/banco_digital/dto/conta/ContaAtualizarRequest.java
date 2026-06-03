package br.com.dbserver.banco_digital.dto.conta;

import jakarta.validation.constraints.NotBlank;

public record ContaAtualizarRequest(
    @NotBlank(message = "O nome do titular é obrigatório para atualização") 
    String nomeTitular
) {}