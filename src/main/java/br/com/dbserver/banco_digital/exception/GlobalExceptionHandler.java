package br.com.dbserver.banco_digital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tools.jackson.databind.exc.UnrecognizedPropertyException;

import br.com.dbserver.banco_digital.exception.especies.ContaNaoEncontradaException;
import br.com.dbserver.banco_digital.exception.especies.SaldoInsuficienteException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContaNaoEncontradaException.class)
    public ResponseEntity<String> handleContaNaoEncontradaException(ContaNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<String> handleSaldoInsuficienteException(SaldoInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        if (ex.getCause() instanceof UnrecognizedPropertyException unrecognizedPropertyException) {
            String mensagem = String.format("O campo '%s' não é permitido nesta requisição.",
                    unrecognizedPropertyException.getPropertyName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagem);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Erro na leitura do corpo da requisição: JSON malformado.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String mensagemErro = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Erro de validação nos dados enviados.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagemErro);
    }
}
