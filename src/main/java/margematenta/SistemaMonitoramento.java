package margematenta;

import java.util.List;
import java.util.Scanner;
import margematenta.dominio.DadosTerritorio;
import margematenta.dominio.EstacaoMonitoramento;
import margematenta.dominio.Medicao;
import margematenta.dominio.Sensor;
import margematenta.dominio.SensorChuva;
import margematenta.dominio.SensorNivelCorrego;
import margematenta.dominio.SensorUmidadeSolo;
import margematenta.excecao.DadosInvalidosException;
import margematenta.excecao.FonteIndisponivelException;

public class SistemaMonitoramento {

    public static void main(String[] args) {
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
                System.out.println("0 - Encerrar");
                System.out.print("Escolha uma opção: ");

                if (!scanner.hasNextLine()) {
                    break;
                }

                String opcao = scanner.nextLine().trim();

                switch (opcao) {
                    case "1" -> verificarSensores(estacao);
                    case "0" -> executando = false;
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }
            }

            System.out.println("Sistema encerrado.");
        }
    }

    private static void verificarSensores(EstacaoMonitoramento estacao) {
        estacao.verificarStatus();
        System.out.printf("%n%s | %s | %s%n",
                estacao.getNome(), estacao.getRegiao(), estacao.getStatus());

        for (Sensor sensor : estacao.getSensores()) {
            try {
                Medicao medicao = estacao.coletarMedicao(sensor);
                System.out.printf("%s: %.1f %s%n",
                        medicao.tipo().getDescricao(),
                        medicao.valor(),
                        medicao.tipo().getUnidade());
            } catch (DadosInvalidosException | FonteIndisponivelException erro) {
                System.out.println(erro.getMessage());
            }
        }
    }
}