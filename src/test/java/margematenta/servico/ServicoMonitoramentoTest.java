package margematenta.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import margematenta.dominio.Alerta;
import margematenta.dominio.DadosTerritorio;
import margematenta.dominio.Medicao;
import margematenta.dominio.NivelAlerta;
import margematenta.dominio.PrevisaoMediaMovel;
import margematenta.dominio.TipoSensor;
import margematenta.persistencia.AlertaDAO;
import margematenta.persistencia.MedicaoDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicoMonitoramentoTest {

    @Mock
    private MedicaoDAO medicaoDAO;

    @Mock
    private AlertaDAO alertaDAO;

    @Test
    void salvaAlertaCriticoQuandoPrevisaoAtingeLimiarCritico() throws SQLException {
        TipoSensor tipo = TipoSensor.CHUVA;
        Medicao medicao = new Medicao(tipo, 65, Instant.EPOCH);
        when(medicaoDAO.buscarHistoricoPorSensor(tipo))
                .thenReturn(Collections.nCopies(5, medicao));
        DadosTerritorio territorio = new DadosTerritorio("Vila das Pontes", 2500, "1234");
        ServicoMonitoramento servico = new ServicoMonitoramento(medicaoDAO, alertaDAO, territorio);

        ServicoMonitoramento.ResultadoPrevisao resultado =
                servico.executarPrevisao(tipo, new PrevisaoMediaMovel());

        Alerta alerta = resultado.alerta().orElseThrow();
        assertEquals(tipo, alerta.tipo());
        assertEquals(NivelAlerta.CRITICO, alerta.nivel());
        assertEquals(65.0, alerta.valorPrevisto(), 0.001);
        assertEquals(65.0, alerta.limiarAtingido(), 0.001);
        verify(alertaDAO).salvar(alerta);
    }

    @Test
    void deixaHistoricoDeAlertasIntactoQuandoPrevisaoFicaAbaixoDaAtencao()
            throws SQLException {
        TipoSensor tipo = TipoSensor.CHUVA;
        Medicao medicao = new Medicao(tipo, 10, Instant.EPOCH);
        when(medicaoDAO.buscarHistoricoPorSensor(tipo))
                .thenReturn(Collections.nCopies(5, medicao));
        DadosTerritorio territorio = new DadosTerritorio("Vila das Pontes", 2500, "1234");
        ServicoMonitoramento servico = new ServicoMonitoramento(medicaoDAO, alertaDAO, territorio);

        ServicoMonitoramento.ResultadoPrevisao resultado =
                servico.executarPrevisao(tipo, new PrevisaoMediaMovel());

        assertEquals(10.0, resultado.valorPrevisto(), 0.001);
        assertTrue(resultado.alerta().isEmpty());
        verifyNoInteractions(alertaDAO);
    }
}