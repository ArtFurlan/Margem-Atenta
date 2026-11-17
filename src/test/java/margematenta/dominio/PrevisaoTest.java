package margematenta.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
import margematenta.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

class PrevisaoTest {

    private static final Instant DATA = Instant.parse("2026-11-17T00:00:00Z");

    @Test
    void calculaMediaDasCincoLeiturasMaisRecentes() {
        List<Medicao> historico = historicoDeChuva();
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel();

        double previsao = estrategia.prever(historico);

        assertEquals(30.0, previsao, 0.001);
    }

    @Test
    void projetaDiferencaEntreAsMediasDosDoisPeriodos() {
        List<Medicao> historico = historicoDeChuva();
        PrevisaoComparacao estrategia = new PrevisaoComparacao();

        double previsao = estrategia.prever(historico);

        assertEquals(70.0, previsao, 0.001);
    }

    @Test
    void rejeitaPrevisaoComMenosDeCincoLeituras() {
        List<Medicao> historico = historicoDeChuva().subList(0, 4);
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel();

        assertThrows(DadosInvalidosException.class, () -> estrategia.prever(historico));
    }

    @Test
    void rejeitaMedicaoNegativa() {
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao(TipoSensor.CHUVA, -1.0, DATA));
    }

    @Test
    void preservaLimiaresQuandoCodigoDeAcessoEstaIncorreto() {
        DadosTerritorio territorio = new DadosTerritorio("Vila das Pontes", 2500, "1234");
        TipoSensor tipo = TipoSensor.CHUVA;
        DadosTerritorio.Limiares anteriores = territorio.getLimiares(tipo);

        assertThrows(DadosInvalidosException.class,
                () -> territorio.alterarLimiares("9999", tipo, 20, 40, 60));

        assertEquals(anteriores, territorio.getLimiares(tipo));
    }

    private List<Medicao> historicoDeChuva() {
        return List.of(
                new Medicao(TipoSensor.CHUVA, 50, DATA),
                new Medicao(TipoSensor.CHUVA, 40, DATA.minusSeconds(60)),
                new Medicao(TipoSensor.CHUVA, 30, DATA.minusSeconds(120)),
                new Medicao(TipoSensor.CHUVA, 20, DATA.minusSeconds(180)),
                new Medicao(TipoSensor.CHUVA, 10, DATA.minusSeconds(240))
        );
    }
}