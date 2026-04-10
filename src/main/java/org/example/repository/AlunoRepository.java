package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.AlunoEntity;
import java.util.List;
import java.util.Optional;

public class AlunoRepository {

    private final EntityManager entityManager;

    public AlunoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public AlunoEntity salvar(AlunoEntity aluno) {
        if (aluno.getId() == null) {
            entityManager.persist(aluno);
            return aluno;
        } else {
            return entityManager.merge(aluno);
        }
    }

    public Optional<AlunoEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(AlunoEntity.class, id));
    }

    public List<AlunoEntity> listarTodos() {
        return entityManager
                .createQuery("SELECT a FROM AlunoEntity a", AlunoEntity.class)
                .getResultList();
    }

    public void deletar(AlunoEntity aluno) {
        entityManager.remove(aluno);
    }

    public void persistirEndereco(Object endereco) {
        entityManager.persist(endereco);
    }
}