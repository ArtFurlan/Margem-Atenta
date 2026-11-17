package margematenta.dominio;

import java.time.Instant;
import java.util.List;
import margematenta.excecao.FonteIndisponivelException;

public class EstacaoMonitoramento extends ComponenteTerritorial {

    private final List<Sensor> sensores;

    public EstacaoMonitoramento(String id, String nome, DadosTerritorio territorio,
                                List<Sensor> sensores) {
        super(id, nome, territorio.getRegiao());
        this.sensores = List.copyOf(sensores);
    }

    public List<Sensor> getSensores() {
        return sensores;
    }

    @Override
    public void verificarStatus() {
        long disponiveis = sensores.stream()
                .filter(Sensor::verificarFuncionamento)
                .count();

        if (disponiveis == 0) {
            atualizarStatus("Indisponível");
        } else if (disponiveis == sensores.size()) {
            atualizarStatus("Operacional");
        } else {
            atualizarStatus("Parcialmente operacional");
        }
    }

    public Medicao coletarMedicao(Sensor sensor) {
        TipoSensor tipo = sensor.retornarTipo();

        if (!sensor.verificarFuncionamento()) {
            throw new FonteIndisponivelException(
                    tipo.getDescricao() + ": sensor indisponível.");
        }

        return new Medicao(tipo, sensor.lerValor(), Instant.now());
    }
}