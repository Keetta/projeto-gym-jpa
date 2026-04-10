package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.PlanoEntity;
import java.util.List;
import java.util.Optional;

public class PlanoRepository {

    private final EntityManager entityManager;

    public PlanoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public PlanoEntity salvar(PlanoEntity plano) {
        if (plano.getId() == null) {

            entityManager.persist(plano);
            return plano;
        } else {
            return entityManager.merge(plano);
        }
    }

    public Optional<PlanoEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(PlanoEntity.class, id));
    }

    public List<PlanoEntity> listarTodos() {
        return entityManager
                .createQuery("SELECT p FROM PlanoEntity p", PlanoEntity.class)
                .getResultList();
    }

    public void deletar(PlanoEntity plano) {
        entityManager.remove(plano);
    }
}
