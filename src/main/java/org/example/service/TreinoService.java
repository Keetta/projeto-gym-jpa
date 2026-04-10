package org.example.service;

import jakarta.persistence.*;
import org.example.entity.TreinadorEntity;
import org.example.entity.TreinoEntity;
import org.example.repository.TreinoRepository;
import java.util.List;

public class TreinoService {

    private EntityManager entityManager;
    private TreinoRepository treinoRepository;

    public TreinoService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.treinoRepository = new TreinoRepository(entityManager);
    }

    public TreinoEntity criarTreino(TreinoEntity treino, Long treinadorId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            if (treino.getNome() == null || treino.getNome().isEmpty()) {
                throw new RuntimeException("Nome é obrigatório!");
            }

            TreinadorEntity treinador = entityManager.find(TreinadorEntity.class, treinadorId);

            if (treinador == null) {
                throw new RuntimeException("Treinador não encontrado!");
            }

            treino.setTreinador(treinador);
            treinoRepository.salvar(treino);
            transaction.commit();
            return treino;

        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public TreinoEntity buscarPorId(Long id) {
        return treinoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Treino não encontrado!"));
    }
    public List<TreinoEntity> listarTreinos() {
        return treinoRepository.listarTodos();
    }

    public TreinoEntity atualizarTreino(Long id, TreinoEntity dadosAtualizados, Long treinadorId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            TreinoEntity treino = treinoRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Treino não encontrado!"));

            treino.setNome(dadosAtualizados.getNome());
            treino.setDescricao(dadosAtualizados.getDescricao());

            if (treinadorId != null) {
                TreinadorEntity treinador = entityManager.find(TreinadorEntity.class, treinadorId);
                if (treinador == null) {
                    throw new RuntimeException("Treinador não encontrado!");
                }

                treino.setTreinador(treinador);
            }

            TreinoEntity atualizado = treinoRepository.salvar(treino);
            transaction.commit();
            return atualizado;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void deletarTreino(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            TreinoEntity treino = treinoRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Treino não encontrado!"));

            treinoRepository.deletar(treino);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}