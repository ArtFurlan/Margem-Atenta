package margematenta.dominio;

import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao {

    public PrevisaoComparacao() {
        super("Comparação de períodos", 5);
    }

    @Override
    public double prever(List<Medicao> historico) {
        validarHistorico(historico);

        double mediaRecente = calcularMedia(historico, 0, 2);
        double mediaAnterior = calcularMedia(historico, 2, 5);
        double previsao = mediaRecente + (mediaRecente - mediaAnterior);
        double maximo = historico.get(0).tipo().getValorMaximo();

        return Math.max(0, Math.min(previsao, maximo));
    }
}