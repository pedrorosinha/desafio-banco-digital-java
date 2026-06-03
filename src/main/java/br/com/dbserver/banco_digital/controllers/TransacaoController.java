package br.com.dbserver.banco_digital.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.dbserver.banco_digital.dto.transacao.TransacaoRequest;
import br.com.dbserver.banco_digital.dto.transacao.TransacaoResponse;
import br.com.dbserver.banco_digital.service.BancoDigitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final BancoDigitalService bancoDigitalService;

    @PostMapping
    public ResponseEntity<TransacaoResponse> realizarTransacao(@Valid @RequestBody TransacaoRequest request) {
        TransacaoResponse response = bancoDigitalService.realizarTransacao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransacaoResponse> buscarPorId(@PathVariable Long id) {
        TransacaoResponse response = bancoDigitalService.buscarTransacaoPorId(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTransacao(@PathVariable Long id) {
        bancoDigitalService.deletarTransacao(id);
        return ResponseEntity.noContent().build();
    }
}