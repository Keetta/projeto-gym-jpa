package org.example.service;

import jakarta.persistence.*;
import org.example.entity.AlunoEntity;
import org.example.entity.MatriculaEntity;
import org.example.entity.PlanoEntity;
import org.example.repository.MatriculaRepository;
import java.time.LocalDateTime;
import java.util.List;

public class MatriculaService {

    public MatriculaService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.matriculaRepository = new MatriculaRepository(entityManager);
    }

    private EntityManager entityManager;
    private MatriculaRepository matriculaRepository;

    private MatriculaEntity criarMatricula(AlunoEntity aluno, PlanoEntity plano) {
        MatriculaEntity m = new MatriculaEntity();
        m.setAluno(aluno);
        m.setPlano(plano);
        m.setDataInicio(LocalDateTime.now());
        m.setStatus("ATIVA");
        return matriculaRepository.salvar(m);
    }

    public boolean alunoJaTemMatriculaAtiva(Long alunoId) {

        String jpql = "SELECT COUNT(m) FROM MatriculaEntity m " +
                "WHERE m.aluno.id = :alunoId AND m.status = 'ATIVA'";

        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("alunoId", alunoId)
                .getSingleResult();
        return count > 0;
    }

    public MatriculaEntity buscarPorId(Long id) {
        return matriculaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Matrícula não encontrada!"));
    }

    public MatriculaEntity matricularAluno (Long alunoId, Long planoId) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            AlunoEntity aluno = entityManager.find(AlunoEntity.class, alunoId);
            PlanoEntity plano = entityManager.find(PlanoEntity.class, planoId);
            // Validações
            if (aluno == null) {
                throw new RuntimeException("Aluno não encontrado!");
            }
            if (plano == null) {
                throw new RuntimeException("Plano não encontrado!");
            }
            if (alunoJaTemMatriculaAtiva(alunoId)) {
                throw new RuntimeException("Aluno já possui matrícula ativa!");
            }

            MatriculaEntity matricula = criarMatricula(aluno, plano);

            transaction.commit();
            return matricula;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void cancelarMatricula (Long matriculaId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            MatriculaEntity matricula = buscarPorId(matriculaId);
            // Validações
            if (!"ATIVA".equals(matricula.getStatus())) {
                throw new RuntimeException("A matrícula não está ativa!");
            }

            matricula.setStatus("CANCELADA");
            matricula.setDataFim(LocalDateTime.now());
            matriculaRepository.salvar(matricula);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public MatriculaEntity buscarMatriculaPorAluno(Long alunoId) {
        AlunoEntity aluno = entityManager.find(AlunoEntity.class, alunoId);
        if (aluno == null) {
            throw new RuntimeException("Aluno não encontrado!");
        }
        String jpql = "SELECT m FROM MatriculaEntity m " +
                "WHERE m.aluno.id = :alunoId AND m.status = 'ATIVA'";

        try {
            return entityManager.createQuery(jpql, MatriculaEntity.class)
                    .setParameter("alunoId", alunoId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new RuntimeException("Aluno não possui matrícula ativa!");
        }
    }

    public List<MatriculaEntity> listarMatriculasPorAluno(Long alunoId, String status) {
        AlunoEntity aluno = entityManager.find(AlunoEntity.class, alunoId);
        if (aluno == null) {
            throw new RuntimeException("Aluno não encontrado!");
        }
        String jpql = "SELECT m FROM MatriculaEntity m " +
                "WHERE m.aluno.id = :alunoId " +
                "AND m.status = :status " +
                "ORDER BY m.dataInicio DESC";

        return entityManager.createQuery(jpql, MatriculaEntity.class)
                .setParameter("alunoId", alunoId)
                .setParameter("status", status)
                .getResultList();
    }

    public MatriculaEntity alterarPlano(Long matriculaId, Long novoPlanoid) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            MatriculaEntity matriculaAtual = buscarPorId(matriculaId);
            // Validações
            if (!"ATIVA".equals(matriculaAtual.getStatus())) {
                throw new RuntimeException("Só é possível alterar plano de matrícula ativa!");
            }
            PlanoEntity novoPlano = entityManager.find(PlanoEntity.class, novoPlanoid);
            if (novoPlano == null) {
                throw new RuntimeException("Plano não encontrado!");
            }

            // Cancela matrícula atual
            matriculaAtual.setStatus("CANCELADA");
            matriculaAtual.setDataFim(LocalDateTime.now());
            // Cria nova matrícula
            MatriculaEntity novaMatricula =
                    criarMatricula(matriculaAtual.getAluno(), novoPlano);

            transaction.commit();
            return novaMatricula;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public MatriculaEntity renovarMatricula(Long matriculaId) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            MatriculaEntity atual = buscarPorId(matriculaId);
            // Validações
            if (!"ATIVA".equals(atual.getStatus())) {
                throw new RuntimeException("Só é possível renovar matrícula ativa!");
            }
            // Encerra a matrícula atual
            atual.setStatus("CANCELADA");
            atual.setDataFim(LocalDateTime.now());
            matriculaRepository.salvar(atual);
            // Cria nova matrícula com o MESMO plano
            MatriculaEntity nova =
                    criarMatricula(atual.getAluno(), atual.getPlano());
            transaction.commit();
            return nova;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void finalizarMatriculasExpiradas() {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            String jpql = "SELECT m FROM MatriculaEntity m " +
                    "WHERE m.status = 'ATIVA' " +
                    "AND m.dataFim IS NOT NULL " +
                    "AND m.dataFim < :agora";

            List<MatriculaEntity> matriculas = entityManager
                    .createQuery(jpql, MatriculaEntity.class)
                    .setParameter("agora", LocalDateTime.now())
                    .getResultList();

            for (MatriculaEntity m : matriculas) {
                m.setStatus("INATIVA");
                matriculaRepository.salvar(m);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public List<MatriculaEntity> listarMatriculasAtivas() {
        return entityManager.createQuery(
                "SELECT m FROM MatriculaEntity m WHERE m.status = 'ATIVA'",
                MatriculaEntity.class
        ).getResultList();
    }

    public boolean matriculaEstaAtiva(Long matriculaId) {
        MatriculaEntity m = buscarPorId(matriculaId);
        return "ATIVA".equals(m.getStatus());
    }

    public List<MatriculaEntity> buscarPorStatus(String status) {
        String jpql = "SELECT m FROM MatriculaEntity m WHERE m.status = :status";

        return entityManager.createQuery(jpql, MatriculaEntity.class)
                .setParameter("status", status)
                .getResultList();
    }
}