package margematenta;

import java.util.Scanner;

public class SistemaMonitoramento {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean executando = true;

            while (executando) {
                System.out.println();
                System.out.println("-- MARGEM ATENTA --");
                System.out.println("Monitoramento de risco de alagamento");
                System.out.println("0 - Encerrar");
                System.out.print("Escolha uma opção: ");

                if (!scanner.hasNextLine()) {
                    break;
                }

                String opcao = scanner.nextLine().trim();

                switch (opcao) {
                    case "0" -> executando = false;
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }
            }

            System.out.println("Sistema encerrado.");
        }
    }
}