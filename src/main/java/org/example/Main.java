package org.example;

import jakarta.persistence.*;
import org.example.entity.*;
import org.example.service.*;
import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("academiaPU");
        EntityManager em = emf.createEntityManager();

        // Todos os Services
        AlunoService alunoService = new AlunoService(em);
        PlanoService planoService = new PlanoService(em);
        MatriculaService matriculaService = new MatriculaService(em);
        PagamentoService pagamentoService = new PagamentoService(em);
        TreinadorService treinadorService = new TreinadorService(em);
        TreinoService treinoService = new TreinoService(em);
        try {
            // Cria Plano
            PlanoEntity plano = new PlanoEntity();
            plano.setNome("BASICO");
            plano.setDescricao("Plano básico mensal");
            plano = planoService.criarPlano(plano);

            // Cria Aluno + Endereço
            EnderecoEntity endereco = new EnderecoEntity();
            endereco.setRua("Rua A");
            endereco.setNumero("123");
            endereco.setCidade("São Paulo");

            AlunoEntity aluno = new AlunoEntity();
            aluno.setNome("Nick");
            aluno.setIdade(20);
            aluno.setTelefone("11999999999");
            aluno.setAltura(new BigDecimal("1.75"));
            aluno.setPeso(new BigDecimal("70.00"));
            aluno.setEndereco(endereco);
            aluno = alunoService.criarAluno(aluno);

            // Matricula o Aluno
            MatriculaEntity matricula = matriculaService
                    .matricularAluno(aluno.getId(), plano.getId());
            // Cria o Pagamento
            PagamentoEntity pagamento = pagamentoService
                    .criarPagamento(matricula.getId(), new BigDecimal("140.00"));
            // Registrando Pagamento
            pagamentoService.registrarPagamento(pagamento.getId());

            // Cria o Treinador
            TreinadorEntity treinador = new TreinadorEntity();
            treinador.setNome("Carlos");
            treinador.setEspecialidade("Hipertrofia");
            treinador.setTelefone("11888888888");
            treinador = treinadorService.criarTreinador(treinador);

            // Cria o Treino
            TreinoEntity treino = new TreinoEntity();
            treino.setNome("Treino A");
            treino.setDescricao("Peito e tríceps");
            treinoService.criarTreino(treino, treinador.getId());

            System.out.println("✅ Sistema executado com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
        } finally {
            em.close();
            emf.close();
        }
    }
}