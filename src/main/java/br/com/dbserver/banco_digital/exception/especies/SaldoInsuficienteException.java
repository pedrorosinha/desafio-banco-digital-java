package br.com.dbserver.banco_digital.exception.especies;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }    
}
