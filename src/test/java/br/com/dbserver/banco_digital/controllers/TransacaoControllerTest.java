package br.com.dbserver.banco_digital.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.dbserver.banco_digital.dto.transacao.TransacaoRequest;
import br.com.dbserver.banco_digital.dto.transacao.TransacaoResponse;
import br.com.dbserver.banco_digital.service.BancoDigitalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

@WebMvcTest(TransacaoController.class)
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    @MockitoBean
    private BancoDigitalService bancoDigitalService;

    @Test
    void deveRetornar201AoRealizarTransacaoComSucesso() throws Exception {
        TransacaoRequest request = new TransacaoRequest(1L, 2L, new BigDecimal("150.00"));
        TransacaoResponse response = new TransacaoResponse(10L, 1L, 2L, new BigDecimal("150.00"), LocalDateTime.now());

        when(bancoDigitalService.realizarTransacao(any(TransacaoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar200AoBuscarTransacaoPorId() throws Exception {
        TransacaoResponse response = new TransacaoResponse(10L, 1L, 2L, new BigDecimal("150.00"), LocalDateTime.now());

        when(bancoDigitalService.buscarTransacaoPorId(10L)).thenReturn(response);

        mockMvc.perform(get("/api/transacoes/10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar204AoDeletarTransacao() throws Exception {
        mockMvc.perform(delete("/api/transacoes/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar400AoEnviarPropriedadeDesconhecidaNaTransacao() throws Exception {
        String jsonComErro = "{\"contaOrigemId\":1,\"contaDestinoId\":2,\"valor\":150.00,\"moeda\":\"BRL\"}";

        mockMvc.perform(post("/api/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonComErro))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O campo 'moeda' não é permitido nesta requisição."));
    }
}