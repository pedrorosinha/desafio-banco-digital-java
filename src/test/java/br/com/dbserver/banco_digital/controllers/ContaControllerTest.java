package br.com.dbserver.banco_digital.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.dbserver.banco_digital.dto.conta.ContaRequest;
import br.com.dbserver.banco_digital.dto.conta.ContaAtualizarRequest;
import br.com.dbserver.banco_digital.dto.conta.ContaResponse;
import br.com.dbserver.banco_digital.service.BancoDigitalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

@WebMvcTest(ContaController.class)
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Configurado com FAIL_ON_UNKNOWN_PROPERTIES para validar o cenário de erro do PUT
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    @MockitoBean
    private BancoDigitalService bancoDigitalService;

    @Test
    void deveRetornar201AoCriarContaValida() throws Exception {
        ContaRequest request = new ContaRequest("Pedro", new BigDecimal("1000.00"));
        ContaResponse response = new ContaResponse(1L, "Pedro", new BigDecimal("1000.00"));

        when(bancoDigitalService.criarConta(any(ContaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeTitular").value("Pedro"))
                .andExpect(jsonPath("$.saldo").value(1000.00));
    }

    @Test
    void deveRetornar200AoBuscarContaPorIdExistente() throws Exception {
        ContaResponse response = new ContaResponse(1L, "Pedro", new BigDecimal("1000.00"));

        when(bancoDigitalService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/contas/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeTitular").value("Pedro"));
    }

    @Test
    void deveRetornar200AoAtualizarNomeComSucessoNoPut() throws Exception {
        ContaAtualizarRequest request = new ContaAtualizarRequest("Pedro Felipe");
        ContaResponse response = new ContaResponse(1L, "Pedro Felipe", new BigDecimal("1000.00"));

        when(bancoDigitalService.atualizarConta(eq(1L), any(ContaAtualizarRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/contas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeTitular").value("Pedro Felipe"))
                .andExpect(jsonPath("$.saldo").value(1000.00)); // O saldo permanece inalterado
    }

    @Test
    void deveRetornar400AoEnviarPropriedadeDesconhecidaNoPut() throws Exception {
        String jsonComErro = "{\"nomeTitular\":\"Pedro\",\"saldoInicial\":1000}";

        mockMvc.perform(put("/api/contas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonComErro))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O campo 'saldoInicial' não é permitido nesta requisição."));
    }

    @Test
    void deveRetornar204AoDeletarContaComSucesso() throws Exception {
        doNothing().when(bancoDigitalService).deletarConta(1L);

        mockMvc.perform(delete("/api/contas/1"))
                .andExpect(status().isNoContent());
    }
}
