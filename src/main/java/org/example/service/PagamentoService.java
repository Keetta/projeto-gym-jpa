package org.example.service;

import jakarta.persistence.*;
import org.example.entity.MatriculaEntity;
import org.example.entity.PagamentoEntity;
import org.example.repository.PagamentoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PagamentoService {
    private EntityManager entityManager;
    private PagamentoRepository pagamentoRepository;

    public PagamentoService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.pagamentoRepository = new PagamentoRepository(entityManager);
    }

    public PagamentoEntity criarPagamento(Long matriculaId, BigDecimal valor) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            MatriculaEntity matricula = entityManager.find(MatriculaEntity.class, matriculaId);
            // Validações
            if (matricula == null) {
                throw new RuntimeException("Matrícula não encontrada!");
            }

            PagamentoEntity pagamento = new PagamentoEntity();
            pagamento.setMatricula(matricula);
            pagamento.setValor(valor);
            pagamento.setStatus("PENDENTE");
            pagamentoRepository.salvar(pagamento);

            transaction.commit();
            return pagamento;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public PagamentoEntity registrarPagamento(Long pagamentoId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            PagamentoEntity pagamento = pagamentoRepository.buscarPorId(pagamentoId)
                    .orElseThrow(() -> new RuntimeException("Pagamento não encontrado!"));
            // Validações
            if ("PAGO".equals(pagamento.getStatus())) {
                throw new RuntimeException("Pagamento já foi realizado!");
            }

            pagamento.setStatus("PAGO");
            pagamento.setDataPagamento(LocalDateTime.now());
            pagamentoRepository.salvar(pagamento);

            transaction.commit();
            return pagamento;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public PagamentoEntity buscarPorId(Long id) {
        return pagamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado!"));
    }

    public List<PagamentoEntity> listarPagamentos() {
        return pagamentoRepository.listarTodos();
    }

    public List<PagamentoEntity> buscarPorStatus(String status) {
        String jpql = "SELECT p FROM PagamentoEntity p WHERE p.status = :status";

        return entityManager.createQuery(jpql, PagamentoEntity.class)
                .setParameter("status", status)
                .getResultList();
    }

    public void deletarPagamento(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            PagamentoEntity pagamento = pagamentoRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Pagamento não encontrado!"));
            pagamentoRepository.deletar(pagamento);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}