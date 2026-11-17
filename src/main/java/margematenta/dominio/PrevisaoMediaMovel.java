package margematenta.dominio;

import java.util.List;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    public PrevisaoMediaMovel() {
        super("Média móvel", 5);
    }

    @Override
    public double prever(List<Medicao> historico) {
        validarHistorico(historico);
        return calcularMedia(historico, 0, 5);
    }
}