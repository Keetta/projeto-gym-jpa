package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.TreinoEntity;
import java.util.List;
import java.util.Optional;

public class TreinoRepository {

    private final EntityManager entityManager;

    public TreinoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TreinoEntity salvar(TreinoEntity treino) {
        if (treino.getId() == null) {
            entityManager.persist(treino);
            return treino;
        } else {
            return entityManager.merge(treino);
        }
    }

    public Optional<TreinoEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(TreinoEntity.class, id));
    }

    public List<TreinoEntity> listarTodos() {
        return entityManager
                .createQuery("SELECT t FROM TreinoEntity t", TreinoEntity.class)
                .getResultList();
    }

    public void deletar(TreinoEntity treino) {
        // Garantindo que o objeto está gerenciado antes de remover
        if (!entityManager.contains(treino)) {
            treino = entityManager.merge(treino);
        }
        entityManager.remove(treino); }

}