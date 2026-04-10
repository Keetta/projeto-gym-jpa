package org.example.service;

import jakarta.persistence.*;
import org.example.entity.PlanoEntity;
import org.example.repository.PlanoRepository;
import java.math.BigDecimal;
import java.util.List;

public class PlanoService {

    private EntityManager entityManager;
    private PlanoRepository planoRepository;

    public PlanoService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.planoRepository = new PlanoRepository(entityManager);
    }

    public PlanoEntity criarPlano(PlanoEntity plano) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            // Validações blablabla
            if (plano.getNome() == null || plano.getNome().isEmpty()) {
                throw new RuntimeException("Nome é obrigatório!");
            }
            if ("BASICO".equalsIgnoreCase(plano.getNome())) {
                plano.setValor(new BigDecimal("140.00"));
            } else if ("PREMIUM".equalsIgnoreCase(plano.getNome())) {
                plano.setValor(new BigDecimal("200.00"));
            } else {
                throw new RuntimeException("Plano inválido!");
            }
            planoRepository.salvar(plano);
            transaction.commit();
            return plano;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public PlanoEntity buscarPorId(Long id) {
        return planoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado!"));
    }

    public List<PlanoEntity> listarPlanos() {
        return planoRepository.listarTodos();
    }

    public PlanoEntity atualizarPlano(Long id, PlanoEntity dadosAtualizados) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            PlanoEntity plano = planoRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Plano não encontrado!"));

            plano.setNome(dadosAtualizados.getNome());
            plano.setDescricao(dadosAtualizados.getDescricao());

            if ("BASICO".equalsIgnoreCase(plano.getNome())) {
                plano.setValor(new BigDecimal("140.00"));
            } else if ("PREMIUM".equalsIgnoreCase(plano.getNome())) {
                plano.setValor(new BigDecimal("200.00"));
            } else {
                throw new RuntimeException("Plano inválido!");
            }
            PlanoEntity atualizado = planoRepository.salvar(plano);
            transaction.commit();
            return atualizado;

        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void deletarPlano(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            PlanoEntity plano = planoRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Plano não encontrado!"));

            planoRepository.deletar(plano);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}