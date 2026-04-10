package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.TreinadorEntity;
import java.util.List;
import java.util.Optional;

public class TreinadorRepository {

    private final EntityManager entityManager;

    public TreinadorRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TreinadorEntity salvar(TreinadorEntity treinador) {
        if (treinador.getId() == null) {
            entityManager.persist(treinador);
            return treinador;
        } else {
            return entityManager.merge(treinador);
        }
    }

    public Optional<TreinadorEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(TreinadorEntity.class, id));
    }

    public List<TreinadorEntity> listarTodos() {
        return entityManager
                .createQuery("SELECT t FROM TreinadorEntity t", TreinadorEntity.class)
                .getResultList();
    }

    public void deletar(TreinadorEntity treinador) { entityManager.remove(treinador);}
}