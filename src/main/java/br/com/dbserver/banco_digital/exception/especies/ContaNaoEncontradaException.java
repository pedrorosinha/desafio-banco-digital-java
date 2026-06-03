package br.com.dbserver.banco_digital.exception.especies;

public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException(String message) {
        super(message);
    }
}
