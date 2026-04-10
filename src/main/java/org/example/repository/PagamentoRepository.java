package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entity.PagamentoEntity;
import java.util.List;
import java.util.Optional;

public class PagamentoRepository {

    private final EntityManager entityManager;

    public PagamentoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public PagamentoEntity salvar(PagamentoEntity pagamento) {
        if (pagamento.getId() == null) {
            entityManager.persist(pagamento);
            return pagamento;
        } else {
            return entityManager.merge(pagamento);
        }
    }

    public Optional<PagamentoEntity> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(PagamentoEntity.class, id));
    }

    public List<PagamentoEntity> listarTodos() {
        return entityManager
                .createQuery("SELECT p FROM PagamentoEntity p", PagamentoEntity.class)
                .getResultList();
    }

    public void deletar(PagamentoEntity pagamento) { entityManager.remove(pagamento);}
}