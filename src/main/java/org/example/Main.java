package org.example;

import jakarta.persistence.*;
import org.example.entity.*;
import org.example.service.*;
import org.flywaydb.core.Flyway;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // Flyway
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:postgresql://localhost:5432/academia", "nick", "nicki12072007")
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();

        System.out.println("Iniciando Flyway...");
        flyway.migrate();
        System.out.println("Flyway OK!");

        // JPA
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("academiaPU");
        EntityManager em = emf.createEntityManager();

        // Services
        AlunoService alunoService = new AlunoService(em);
        PlanoService planoService = new PlanoService(em);
        MatriculaService matriculaService = new MatriculaService(em);
        PagamentoService pagamentoService = new PagamentoService(em);
        TreinadorService treinadorService = new TreinadorService(em);
        TreinoService treinoService = new TreinoService(em);

        int op;

        do {
            System.out.println("\n===== MENU =====");
            System.out.println("1 - Alunos");
            System.out.println("2 - Planos");
            System.out.println("3 - Matrículas");
            System.out.println("4 - Pagamentos");
            System.out.println("5 - Treinadores");
            System.out.println("6 - Treinos");
            System.out.println("0 - Sair");

            op = Integer.parseInt(scanner.nextLine());

            try {
                switch (op) {
                    case 1 -> menuAlunos(alunoService);
                    case 2 -> menuPlanos(planoService);
                    case 3 -> menuMatriculas(matriculaService);
                    case 4 -> menuPagamentos(pagamentoService);
                    case 5 -> menuTreinadores(treinadorService);
                    case 6 -> menuTreinos(treinoService);
                }
            } catch (Exception e) {
                System.out.println("❌ " + e.getMessage());
            }
        } while (op != 0);

        em.close();
        emf.close();
    }

    // -=-=-=-=-=-=- ALUNOS -=-=-=-=-=-=-
    private static void menuAlunos(AlunoService service) {
        int op;
        do {
            System.out.println("\n--- ALUNOS ---");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar");
            System.out.println("3 - Buscar por ID");
            System.out.println("4 - Deletar");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    EnderecoEntity endereco = new EnderecoEntity();

                    System.out.print("Rua: ");
                    endereco.setRua(scanner.nextLine());
                    System.out.print("Número: ");
                    endereco.setNumero(scanner.nextLine());
                    System.out.print("Cidade: ");
                    endereco.setCidade(scanner.nextLine());

                    AlunoEntity aluno = new AlunoEntity();

                    System.out.print("Nome: ");
                    aluno.setNome(scanner.nextLine());
                    System.out.print("Idade: ");
                    aluno.setIdade(Integer.parseInt(scanner.nextLine()));
                    System.out.print("Telefone: ");
                    aluno.setTelefone(scanner.nextLine());
                    System.out.print("Altura: ");
                    aluno.setAltura(new BigDecimal(scanner.nextLine()));
                    System.out.print("Peso: ");
                    aluno.setPeso(new BigDecimal(scanner.nextLine()));

                    aluno.setEndereco(endereco);

                    service.criarAluno(aluno);
                    System.out.println("✅ Aluno criado!");
                }
                case 2 -> {
                    List<AlunoEntity> lista = service.listarAlunos();
                    lista.forEach(a ->
                            System.out.println("[\uD83D\uDC64] ID: " + a.getId() + " | Nome: " + a.getNome() + " | " +
                                    "Telefone: " + a.getTelefone()));
                }
                case 3 -> {
                    System.out.print("ID: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    AlunoEntity a = service.buscarAlunoPorId(id);
                    System.out.println("Nome: " + a.getNome());
                }
                case 4 -> {
                    System.out.print("ID: ");
                    Long id = Long.parseLong(scanner.nextLine());
                    service.deletarAluno(id);
                    System.out.println("🗑️ Deletado!");
                }
            }
        } while (op != 0);
    }

    // -=-=-=-=-=-=- PLANOS -=-=-=-=-=-=-
    private static void menuPlanos(PlanoService service) {
        int op;
        do {
            System.out.println("\n--- PLANOS ---");
            System.out.println("1 - Criar");
            System.out.println("2 - Listar");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    PlanoEntity p = new PlanoEntity();

                    System.out.print("Nome (BASICO/PREMIUM): ");
                    p.setNome(scanner.nextLine());
                    System.out.print("Descrição: ");
                    p.setDescricao(scanner.nextLine());

                    service.criarPlano(p);
                    System.out.println("✅ Plano criado!");
                }
                case 2 -> {
                    service.listarPlanos()
                            .forEach(p -> System.out.println(p.getId() + " - " + p.getNome()));
                }
            }
        } while (op != 0);
    }

    // -=-=-=-=-=-=- MATRÍCULAS -=-=-=-=-=-=-
    private static void menuMatriculas(MatriculaService service) {
        int op;
        do {
            System.out.println("\n--- MATRÍCULAS ---");
            System.out.println("1 - Matricular aluno");
            System.out.println("2 - Cancelar matrícula");
            System.out.println("3 - Listar ativas");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    System.out.print("Aluno ID: ");
                    Long alunoId = Long.parseLong(scanner.nextLine());
                    System.out.print("Plano ID: ");
                    Long planoId = Long.parseLong(scanner.nextLine());

                    service.matricularAluno(alunoId, planoId);
                    System.out.println("✅ Matriculado!");
                }
                case 2 -> {
                    System.out.print("Matrícula ID: ");
                    Long id = Long.parseLong(scanner.nextLine());

                    service.cancelarMatricula(id);
                    System.out.println("❌ Cancelada!");
                }
                case 3 -> {
                    service.listarMatriculasAtivas()
                            .forEach(m -> System.out.println("ID: " + m.getId()));
                }
            }
        } while (op != 0);
    }

    // -=-=-=-=-=-=- PAGAMENTOS -=-=-=-=-=-=-
    private static void menuPagamentos(PagamentoService service) {
        int op;
        do {
            System.out.println("\n--- PAGAMENTOS ---");
            System.out.println("1 - Criar");
            System.out.println("2 - Pagar");
            System.out.println("3 - Listar");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    System.out.print("Matrícula ID: ");
                    Long id = Long.parseLong(scanner.nextLine());

                    System.out.print("Valor: ");
                    BigDecimal valor = new BigDecimal(scanner.nextLine());

                    service.criarPagamento(id, valor);
                    System.out.println("\uD83D\uDCB8 Criado!");
                }
                case 2 -> {
                    System.out.print("Pagamento ID: ");
                    Long id = Long.parseLong(scanner.nextLine());

                    service.registrarPagamento(id);
                    System.out.println("✅ Pago!");
                }
                case 3 -> {
                    service.listarPagamentos()
                            .forEach(p -> System.out.println("[\uD83D\uDCB8] ID: " + p.getId() + " | " + p.getValor() + " | " + p.getStatus()));
                }
            }
        } while (op != 0);
    }

    // -=-=-=-=-=-=- TREINADORES -=-=-=-=-=-=-
    private static void menuTreinadores(TreinadorService service) {
        int op;
        do {
            System.out.println("\n--- TREINADORES ---");
            System.out.println("1 - Criar");
            System.out.println("2 - Listar");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    TreinadorEntity t = new TreinadorEntity();

                    System.out.print("Nome: ");
                    t.setNome(scanner.nextLine());

                    System.out.print("Especialidade: ");
                    t.setEspecialidade(scanner.nextLine());

                    System.out.print("Telefone: ");
                    t.setTelefone(scanner.nextLine());

                    service.criarTreinador(t);
                    System.out.println("✅ Criado!");
                }
                case 2 -> {
                    service.listarTreinadores()
                            .forEach(t -> System.out.println("[\uD83D\uDC64] ID: " + t.getId() + " - " + t.getNome() + " - " + t.getEspecialidade()));
                }
            }
        } while (op != 0);
    }

    // -=-=-=-=-=-=- TREINOS -=-=-=-=-=-=-
    private static void menuTreinos(TreinoService service) {
        int op;
        do {
            System.out.println("\n--- TREINOS ---");
            System.out.println("1 - Criar");
            System.out.println("2 - Listar");
            System.out.println("0 - Voltar");

            op = Integer.parseInt(scanner.nextLine());

            switch (op) {
                case 1 -> {
                    TreinoEntity t = new TreinoEntity();

                    System.out.print("Nome: ");
                    t.setNome(scanner.nextLine());
                    System.out.print("Descrição: ");
                    t.setDescricao(scanner.nextLine());
                    System.out.print("Treinador ID: ");
                    Long treinadorId = Long.parseLong(scanner.nextLine());

                    service.criarTreino(t, treinadorId);
                    System.out.println("\uD83C\uDFCB\uFE0F Criado!");
                }
                case 2 -> {
                    service.listarTreinos()
                            .forEach(t -> System.out.println("[\uD83C\uDFCB\uFE0F] ID: " + t.getId() + " - " + t.getNome() + " - [\uD83D\uDC64] Treinador: " + t.getTreinador().getNome()));
                }
            }
        } while (op != 0);
    }
}