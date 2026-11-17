package margematenta.servico;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import margematenta.dominio.Alerta;
import margematenta.dominio.DadosTerritorio;
import margematenta.dominio.EstrategiaPrevisao;
import margematenta.dominio.Medicao;
import margematenta.dominio.NivelAlerta;
import margematenta.dominio.TipoSensor;
import margematenta.persistencia.AlertaDAO;
import margematenta.persistencia.MedicaoDAO;

public class ServicoMonitoramento {

    private final MedicaoDAO medicaoDAO;
    private final AlertaDAO alertaDAO;
    private final DadosTerritorio territorio;

    public ServicoMonitoramento(MedicaoDAO medicaoDAO, AlertaDAO alertaDAO,
                                DadosTerritorio territorio) {
        this.medicaoDAO = medicaoDAO;
        this.alertaDAO = alertaDAO;
        this.territorio = territorio;
    }

    public ResultadoPrevisao executarPrevisao(TipoSensor tipo,
                                              EstrategiaPrevisao estrategia)
            throws SQLException {
        List<Medicao> historico = medicaoDAO.buscarHistoricoPorSensor(tipo);
        double valorPrevisto = estrategia.prever(historico);
        DadosTerritorio.Limiares limiares = territorio.getLimiares(tipo);

        NivelAlerta nivel;
        double limiar;

        if (valorPrevisto >= limiares.critico()) {
            nivel = NivelAlerta.CRITICO;
            limiar = limiares.critico();
        } else if (valorPrevisto >= limiares.alerta()) {
            nivel = NivelAlerta.ALERTA;
            limiar = limiares.alerta();
        } else if (valorPrevisto >= limiares.atencao()) {
            nivel = NivelAlerta.ATENCAO;
            limiar = limiares.atencao();
        } else {
            return new ResultadoPrevisao(valorPrevisto, Optional.empty());
        }

        Alerta alerta = new Alerta(tipo, nivel, valorPrevisto, limiar,
                estrategia.getNome(), Instant.now());
        alertaDAO.salvar(alerta);

        return new ResultadoPrevisao(valorPrevisto, Optional.of(alerta));
    }

    public record ResultadoPrevisao(double valorPrevisto, Optional<Alerta> alerta) {
    }
}