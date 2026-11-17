package margematenta;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import margematenta.dominio.Alerta;
import margematenta.dominio.DadosTerritorio;
import margematenta.dominio.EstacaoMonitoramento;
import margematenta.dominio.EstrategiaPrevisao;
import margematenta.dominio.Medicao;
import margematenta.dominio.Sensor;
import margematenta.dominio.SensorChuva;
import margematenta.dominio.SensorNivelCorrego;
import margematenta.dominio.SensorUmidadeSolo;
import margematenta.dominio.TipoSensor;
import margematenta.excecao.DadosInvalidosException;
import margematenta.excecao.FonteIndisponivelException;
import margematenta.persistencia.AlertaDAO;
import margematenta.persistencia.MedicaoDAO;
import margematenta.servico.FabricaPrevisao;
import margematenta.servico.ServicoMonitoramento;

public class SistemaMonitoramento {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    public static void main(String[] args) {
        MedicaoDAO medicaoDAO = new MedicaoDAO();
        AlertaDAO alertaDAO = new AlertaDAO();

        try {
            Files.createDirectories(Path.of("dados"));
            medicaoDAO.criarTabela();
            alertaDAO.criarTabela();
        } catch (IOException | SQLException erro) {
            System.out.println("Erro ao iniciar o banco de dados: " + erro.getMessage());
            return;
        }

        DadosTerritorio territorio = new DadosTerritorio(
                "Vila das Pontes", 2500, "1234");

        EstacaoMonitoramento estacao = new EstacaoMonitoramento(
                "EST-01",
                "Estação Margem Norte",
                territorio,
                List.of(
                        new SensorChuva(true),
                        new SensorNivelCorrego(true),
                        new SensorUmidadeSolo(true)
                )
        );

        FabricaPrevisao fabrica = new FabricaPrevisao();
        ServicoMonitoramento servico =
                new ServicoMonitoramento(medicaoDAO, alertaDAO, territorio);

        try (Scanner scanner = new Scanner(System.in)) {
            boolean executando = true;

            while (executando) {
                System.out.println();
                System.out.println("-- MARGEM ATENTA --");
                System.out.println("Monitoramento de risco de alagamento");
                System.out.println("1 - Verificar sensores");
                System.out.println("2 - Consultar histórico de medições");
                System.out.println("3 - Executar previsão");
                System.out.println("4 - Consultar histórico de alertas");
                System.out.println("0 - Encerrar");
                System.out.print("Escolha uma opção: ");

                if (!scanner.hasNextLine()) {
                    break;
                }

                String opcao = scanner.nextLine().trim();

                switch (opcao) {
                    case "1" -> verificarSensores(estacao, medicaoDAO);
                    case "2" -> consultarHistorico(scanner, medicaoDAO);
                    case "3" -> executarPrevisao(scanner, fabrica, servico);
                    case "4" -> consultarAlertas(alertaDAO);
                    case "0" -> executando = false;
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }
            }

            System.out.println("Sistema encerrado.");
        }
    }

    private static void verificarSensores(EstacaoMonitoramento estacao,
                                          MedicaoDAO medicaoDAO) {
        estacao.verificarStatus();
        System.out.printf("%n%s | %s | %s%n",
                estacao.getNome(), estacao.getRegiao(), estacao.getStatus());

        for (Sensor sensor : estacao.getSensores()) {
            try {
                Medicao medicao = estacao.coletarMedicao(sensor);
                medicaoDAO.salvar(medicao);

                System.out.printf("%s: %.1f %s%n",
                        medicao.tipo().getDescricao(),
                        medicao.valor(),
                        medicao.tipo().getUnidade());
            } catch (DadosInvalidosException | FonteIndisponivelException erro) {
                System.out.println(erro.getMessage());
            } catch (SQLException erro) {
                System.out.printf("Erro ao salvar a medição de %s: %s%n",
                        sensor.retornarTipo().getDescricao(), erro.getMessage());
            }
        }
    }

    private static void consultarHistorico(Scanner scanner, MedicaoDAO medicaoDAO) {
        TipoSensor tipo = escolherSensor(scanner);

        if (tipo == null) {
            return;
        }

        try {
            List<Medicao> historico = medicaoDAO.buscarHistoricoPorSensor(tipo);
            System.out.printf("%nHistórico: %s%n", tipo.getDescricao());

            if (historico.isEmpty()) {
                System.out.println("Nenhuma medição registrada para este sensor.");
                return;
            }

            for (Medicao medicao : historico) {
                System.out.printf("%s | %.1f %s%n",
                        FORMATO_DATA.format(medicao.dataHora()),
                        medicao.valor(),
                        tipo.getUnidade());
            }
        } catch (SQLException | DadosInvalidosException erro) {
            System.out.println("Erro ao consultar o histórico: " + erro.getMessage());
        }
    }

    private static void executarPrevisao(Scanner scanner, FabricaPrevisao fabrica,
                                         ServicoMonitoramento servico) {
        TipoSensor tipo = escolherSensor(scanner);

        if (tipo == null) {
            return;
        }

        System.out.println();
        System.out.println("1 - Média móvel");
        System.out.println("2 - Comparação de períodos");
        System.out.println("0 - Voltar");
        System.out.print("Escolha a estratégia: ");

        int escolha = lerOpcao(scanner, 2);

        if (escolha == 0) {
            return;
        }

        FabricaPrevisao.Metodo metodo = FabricaPrevisao.Metodo.values()[escolha - 1];
        EstrategiaPrevisao estrategia = fabrica.criar(metodo);

        try {
            ServicoMonitoramento.ResultadoPrevisao resultado =
                    servico.executarPrevisao(tipo, estrategia);

            System.out.printf("%n%s | %s | Próxima leitura estimada: %.2f %s%n",
                    tipo.getDescricao(),
                    estrategia.getNome(),
                    resultado.valorPrevisto(),
                    tipo.getUnidade());

            if (resultado.alerta().isPresent()) {
                exibirAlerta(resultado.alerta().get());
            } else {
                System.out.println("Previsão abaixo do limiar de atenção.");
            }
        } catch (DadosInvalidosException erro) {
            System.out.println("Previsão indisponível: " + erro.getMessage());
        } catch (SQLException erro) {
            System.out.println("Erro ao executar a previsão: " + erro.getMessage());
        }
    }

    private static void consultarAlertas(AlertaDAO alertaDAO) {
        try {
            List<Alerta> alertas = alertaDAO.listarTodos();

            if (alertas.isEmpty()) {
                System.out.println("Nenhum alerta registrado.");
                return;
            }

            System.out.println("\nHistórico de alertas");

            for (Alerta alerta : alertas) {
                exibirAlerta(alerta);
            }
        } catch (SQLException erro) {
            System.out.println("Erro ao consultar os alertas: " + erro.getMessage());
        }
    }

    private static void exibirAlerta(Alerta alerta) {
        String unidade = alerta.tipo().getUnidade();

        System.out.printf(
                "%s | %s | %s | previsão de %.2f %s atingiu o limiar de %.2f %s | %s%n",
                FORMATO_DATA.format(alerta.dataHora()),
                alerta.nivel().getDescricao(),
                alerta.tipo().getDescricao(),
                alerta.valorPrevisto(),
                unidade,
                alerta.limiarAtingido(),
                unidade,
                alerta.estrategia()
        );
    }

    private static TipoSensor escolherSensor(Scanner scanner) {
        TipoSensor[] tipos = TipoSensor.values();

        System.out.println();
        for (int indice = 0; indice < tipos.length; indice++) {
            System.out.printf("%d - %s%n", indice + 1, tipos[indice].getDescricao());
        }
        System.out.println("0 - Voltar");
        System.out.print("Escolha um sensor: ");

        int escolha = lerOpcao(scanner, tipos.length);
        return escolha == 0 ? null : tipos[escolha - 1];
    }

    private static int lerOpcao(Scanner scanner, int maximo) {
        if (!scanner.hasNextLine()) {
            return 0;
        }

        try {
            int escolha = Integer.parseInt(scanner.nextLine().trim());

            if (escolha >= 0 && escolha <= maximo) {
                return escolha;
            }
        } catch (NumberFormatException erro) {
            System.out.println("Digite o número de uma das opções.");
            return 0;
        }

        System.out.println("Opção inválida.");
        return 0;
    }
}