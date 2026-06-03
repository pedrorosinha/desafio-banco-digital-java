package br.com.dbserver.banco_digital.service;

import java.math.BigDecimal;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.com.dbserver.banco_digital.models.Conta;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificacaoService {

    @Async
    public void enviarNotificacaoTransferencia(Conta origem, Conta destino, BigDecimal valor) {
        try {
            Thread.sleep(2000);

            log.info("Notificação enviada com sucesso para {}! Você realizou uma transferência de R$ {} para {}!",
                    origem.getNomeTitular(), valor, destino.getNomeTitular());
        } catch (InterruptedException e) {
            log.error("Falha ao enviar notificação de transferência", e);
            Thread.currentThread().interrupt();
        }
    }
}
