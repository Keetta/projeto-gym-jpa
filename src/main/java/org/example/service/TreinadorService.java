package org.example.service;

import jakarta.persistence.*;
import org.example.entity.TreinadorEntity;
import org.example.repository.TreinadorRepository;
import java.util.List;

public class TreinadorService {
    private EntityManager entityManager;
    private TreinadorRepository treinadorRepository;

    public TreinadorService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.treinadorRepository = new TreinadorRepository(entityManager);
    }

    public TreinadorEntity criarTreinador(TreinadorEntity treinador) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            // Tá validando ali ó
            if (treinador.getNome() == null || treinador.getNome().isEmpty()) {
                throw new RuntimeException("Nome é obrigatório!");
            }
            if (treinador.getEspecialidade() == null || treinador.getEspecialidade().isEmpty()) {
                throw new RuntimeException("Especialidade é obrigatória!");
            }
            if (treinador.getTelefone() == null || treinador.getTelefone().isEmpty()) {
                throw new RuntimeException("Telefone é obrigatório!");
            }
            treinadorRepository.salvar(treinador);
            transaction.commit();
            return treinador;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public TreinadorEntity buscarPorId(Long id) {
        return treinadorRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Treinador não encontrado!"));
    }

    public List<TreinadorEntity> listarTreinadores() {
        return treinadorRepository.listarTodos();
    }

    public TreinadorEntity atualizarTreinador(Long id, TreinadorEntity dadosAtualizados) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            TreinadorEntity treinador = treinadorRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Treinador não encontrado!"));

            treinador.setNome(dadosAtualizados.getNome());
            treinador.setEspecialidade(dadosAtualizados.getEspecialidade());
            treinador.setTelefone(dadosAtualizados.getTelefone());
            TreinadorEntity atualizado = treinadorRepository.salvar(treinador);

            transaction.commit();
            return atualizado;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void deletarTreinador(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            TreinadorEntity treinador = treinadorRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Treinador não encontrado!"));

            treinadorRepository.deletar(treinador);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}