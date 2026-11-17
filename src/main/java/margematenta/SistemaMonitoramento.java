package margematenta;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import margematenta.dominio.DadosTerritorio;
import margematenta.dominio.EstacaoMonitoramento;
import margematenta.dominio.Medicao;
import margematenta.dominio.Sensor;
import margematenta.dominio.SensorChuva;
import margematenta.dominio.SensorNivelCorrego;
import margematenta.dominio.SensorUmidadeSolo;
import margematenta.dominio.TipoSensor;
import margematenta.excecao.DadosInvalidosException;
import margematenta.excecao.FonteIndisponivelException;
import margematenta.persistencia.MedicaoDAO;

public class SistemaMonitoramento {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    public static void main(String[] args) {
        MedicaoDAO medicaoDAO = new MedicaoDAO();

        try {
            Files.createDirectories(Path.of("dados"));
            medicaoDAO.criarTabela();
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

        try (Scanner scanner = new Scanner(System.in)) {
            boolean executando = true;

            while (executando) {
                System.out.println();
                System.out.println("-- MARGEM ATENTA --");
                System.out.println("Monitoramento de risco de alagamento");
                System.out.println("1 - Verificar sensores");
                System.out.println("2 - Consultar histórico de medições");
                System.out.println("0 - Encerrar");
                System.out.print("Escolha uma opção: ");

                if (!scanner.hasNextLine()) {
                    break;
                }

                String opcao = scanner.nextLine().trim();

                switch (opcao) {
                    case "1" -> verificarSensores(estacao, medicaoDAO);
                    case "2" -> consultarHistorico(scanner, medicaoDAO);
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
        TipoSensor[] tipos = TipoSensor.values();

        System.out.println();
        for (int indice = 0; indice < tipos.length; indice++) {
            System.out.printf("%d - %s%n", indice + 1, tipos[indice].getDescricao());
        }
        System.out.println("0 - Voltar");
        System.out.print("Escolha um sensor: ");

        if (!scanner.hasNextLine()) {
            return;
        }

        String entrada = scanner.nextLine().trim();
        int escolha;

        try {
            escolha = Integer.parseInt(entrada);
        } catch (NumberFormatException erro) {
            System.out.println("Opção de sensor inválida.");
            return;
        }

        if (escolha == 0) {
            return;
        }
        if (escolha < 1 || escolha > tipos.length) {
            System.out.println("Opção de sensor inválida.");
            return;
        }

        TipoSensor tipo = tipos[escolha - 1];

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
}