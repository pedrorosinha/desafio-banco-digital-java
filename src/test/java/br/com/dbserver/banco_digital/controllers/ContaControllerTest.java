package br.com.dbserver.banco_digital.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.dbserver.banco_digital.dto.conta.ContaRequest;
import br.com.dbserver.banco_digital.dto.conta.ContaResponse;
import br.com.dbserver.banco_digital.service.BancoDigitalService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ContaController.class)
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
                .andExpect(status().isCreated());
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
}