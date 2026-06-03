package br.com.dbserver.banco_digital.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.dbserver.banco_digital.dto.conta.ContaRequest;
import br.com.dbserver.banco_digital.dto.conta.ContaResponse;
import br.com.dbserver.banco_digital.service.BancoDigitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
public class ContaController {

    private final BancoDigitalService bancoDigitalService;

    @PostMapping
    public ResponseEntity<ContaResponse> criarConta(@Valid @RequestBody ContaRequest request) {
        ContaResponse response = bancoDigitalService.criarConta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponse> buscarPorId(@PathVariable Long id) {
        ContaResponse response = bancoDigitalService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaResponse> atualizarConta(@PathVariable Long id,
            @Valid @RequestBody ContaRequest request) {
        ContaResponse response = bancoDigitalService.atualizarConta(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarConta(@PathVariable Long id) {
        bancoDigitalService.deletarConta(id);
        return ResponseEntity.noContent().build();
    }
}