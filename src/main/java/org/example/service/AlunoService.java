package org.example.service;

import jakarta.persistence.*;
import java.util.List;
import org.example.entity.AlunoEntity;

public class AlunoService {

    private EntityManager entityManager;

    public AlunoService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public AlunoEntity criarAluno(AlunoEntity aluno) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            // Validações
            if (aluno.getNome() == null || aluno.getNome().isEmpty()) {
                throw new RuntimeException("Nome é obrigatório!");
            }
            if (aluno.getEndereco() == null) {
                throw new RuntimeException("Endereço é obrigatório!");
            }
            // Salvar Endereço
            entityManager.persist(aluno.getEndereco());
            // Salvar Aluno
            entityManager.persist(aluno);

            transaction.commit();
            return aluno;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public AlunoEntity buscarAlunoPorId(Long id) {
        AlunoEntity aluno = entityManager.find(AlunoEntity.class, id);

        if (aluno == null) {
            throw new RuntimeException("Aluno não foi encontrado!");
        }
        return aluno;
    }

    public List<AlunoEntity> listarAlunos() {
        return entityManager
                .createQuery("SELECT a FROM AlunoEntity a", AlunoEntity.class)
                .getResultList();
    }

    public AlunoEntity atualizarAluno(Long id, AlunoEntity dadosAtualizados) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            // Buscar um aluno existente
            AlunoEntity aluno = entityManager.find(AlunoEntity.class, id);

            if (aluno == null) {
                throw new RuntimeException("Aluno não encontrado!");
            }

            // 🔹 Atualiza os dados com os setters
            aluno.setNome(dadosAtualizados.getNome());
            aluno.setIdade(dadosAtualizados.getIdade());
            aluno.setTelefone(dadosAtualizados.getTelefone());
            aluno.setAltura(dadosAtualizados.getAltura());
            aluno.setPeso(dadosAtualizados.getPeso());

            if (dadosAtualizados.getEndereco() != null) {
                aluno.setEndereco(dadosAtualizados.getEndereco());
            }

            // Finalmente o merge, sincronizando com o banco
            AlunoEntity alunoAtualizado = entityManager.merge(aluno);
            transaction.commit();
            return alunoAtualizado;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void deletarAluno(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            // Buscar um aluno existente
            AlunoEntity aluno = entityManager.find(AlunoEntity.class, id);

            if (aluno == null) {
                throw new RuntimeException("Aluno não encontrado!");
            }

            // Deletar, deletar, deletar (do banco)
            entityManager.remove(aluno);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}
