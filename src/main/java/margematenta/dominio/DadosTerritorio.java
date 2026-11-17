package margematenta.dominio;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import margematenta.excecao.DadosInvalidosException;

public class DadosTerritorio {

    private final String regiao;
    private final String codigoAcesso;
    private final Map<TipoSensor, Limiares> limiares =
            new EnumMap<>(TipoSensor.class);
    private int populacao;

    public DadosTerritorio(String regiao, int populacao, String codigoAcesso) {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("Informe a região monitorada.");
        }
        if (codigoAcesso == null || codigoAcesso.isBlank()) {
            throw new DadosInvalidosException("Informe um código de acesso.");
        }

        this.regiao = regiao;
        this.codigoAcesso = codigoAcesso;
        setPopulacao(populacao);

        limiares.put(TipoSensor.CHUVA, new Limiares(25, 45, 65));
        limiares.put(TipoSensor.NIVEL_CORREGO, new Limiares(1.0, 1.5, 2.0));
        limiares.put(TipoSensor.UMIDADE_SOLO, new Limiares(60, 75, 90));
    }

    public String getRegiao() {
        return regiao;
    }

    public int getPopulacao() {
        return populacao;
    }

    public void setPopulacao(int populacao) {
        if (populacao < 0) {
            throw new DadosInvalidosException("A população não pode ser negativa.");
        }
        this.populacao = populacao;
    }

    public Limiares getLimiares(TipoSensor tipo) {
        return limiares.get(tipo);
    }

    public void alterarLimiares(String codigoInformado, TipoSensor tipo,
                                double atencao, double alerta, double critico) {
        if (!Objects.equals(codigoAcesso, codigoInformado)) {
            throw new DadosInvalidosException("Código de acesso incorreto.");
        }

        if (tipo == null || !tipo.aceitaValor(atencao)
                || !tipo.aceitaValor(alerta) || !tipo.aceitaValor(critico)
                || atencao >= alerta || alerta >= critico) {
            throw new DadosInvalidosException(
                    "Os limiares devem estar na faixa do sensor e seguir "
                            + "a ordem atenção, alerta e crítico.");
        }

        limiares.put(tipo, new Limiares(atencao, alerta, critico));
    }

    public record Limiares(double atencao, double alerta, double critico) {
    }
}