package br.com.dbserver.banco_digital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.dbserver.banco_digital.models.Transacao;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByContaOrigemIdOrContaDestinoIdOrderByDataHoraDesc(Long contaOrigemId, Long contaDestinoId);
}
