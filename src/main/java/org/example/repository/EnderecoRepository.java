package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.EnderecoEntity;
import java.util.List;
import java.util.Optional;

public class EnderecoRepository {

    private final EntityManager entityManager;

    public EnderecoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EnderecoEntity salvar(EnderecoEntity endereco) {
        if (endereco.getId() == null) {
            entityManager.persist(endereco);
            return endereco;
        } else {
            return entityManager.merge(endereco);
        }
    }

    public Optional<EnderecoEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(EnderecoEntity.class, id));
    }

    public List<EnderecoEntity> listarTodos() {
        return entityManager
                .createQuery("SELECT e FROM EnderecoEntity e", EnderecoEntity.class)
                .getResultList();
    }

    public void deletar(EnderecoEntity endereco) { entityManager.remove(endereco); }
}