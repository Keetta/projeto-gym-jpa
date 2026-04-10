package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.MatriculaEntity;
import java.util.List;
import java.util.Optional;

public class MatriculaRepository {

    private final EntityManager entityManager;

    public MatriculaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public MatriculaEntity salvar(MatriculaEntity matricula) {
        if (matricula.getId() == null) {
            entityManager.persist(matricula);
            return matricula;
        } else {
            return entityManager.merge(matricula);
        }
    }

    public Optional<MatriculaEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(MatriculaEntity.class, id));
    }

    public List<MatriculaEntity> listarTodas() {
        return entityManager
                .createQuery("SELECT m FROM MatriculaEntity m", MatriculaEntity.class)
                .getResultList();
    }

    public void deletar(MatriculaEntity matricula) { entityManager.remove(matricula);}
}