package org.example.service;

import jakarta.persistence.*;
import java.util.List;
import org.example.entity.AlunoEntity;
import org.example.repository.AlunoRepository;
import org.example.repository.EnderecoRepository;

public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final EnderecoRepository enderecoRepository;
    private final EntityManager entityManager;

    public AlunoService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.alunoRepository = new AlunoRepository(entityManager);
        this.enderecoRepository = new EnderecoRepository(entityManager);
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
            // Salvar endereço
            enderecoRepository.salvar(aluno.getEndereco());
            // Salvar aluno
            alunoRepository.salvar(aluno);
            transaction.commit();
            return aluno;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public AlunoEntity buscarAlunoPorId(Long id) {
        return alunoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado!"));
    }

    public List<AlunoEntity> listarAlunos() {
        return alunoRepository.listarTodos();
    }

    public AlunoEntity atualizarAluno(Long id, AlunoEntity dadosAtualizados) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            AlunoEntity aluno = alunoRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Aluno não encontrado!"));

            aluno.setNome(dadosAtualizados.getNome());
            aluno.setIdade(dadosAtualizados.getIdade());
            aluno.setTelefone(dadosAtualizados.getTelefone());
            aluno.setAltura(dadosAtualizados.getAltura());
            aluno.setPeso(dadosAtualizados.getPeso());

            if (dadosAtualizados.getEndereco() != null) {
                aluno.setEndereco(dadosAtualizados.getEndereco());
            }

            AlunoEntity atualizado = alunoRepository.salvar(aluno);

            transaction.commit();
            return atualizado;

        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void deletarAluno(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            AlunoEntity aluno = alunoRepository.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Aluno não encontrado!"));

            alunoRepository.deletar(aluno);
            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}
