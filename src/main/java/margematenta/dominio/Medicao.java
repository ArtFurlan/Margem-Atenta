package margematenta.dominio;

import java.time.Instant;
import margematenta.excecao.DadosInvalidosException;

public record Medicao(TipoSensor tipo, double valor, Instant dataHora) {

    public Medicao {
        if (tipo == null || dataHora == null) {
            throw new DadosInvalidosException(
                    "A medição precisa ter um tipo de sensor e uma data.");
        }

        if (!tipo.aceitaValor(valor)) {
            throw new DadosInvalidosException(
                    "Medição fora da faixa para " + tipo.getDescricao()
                            + ". Informe um valor entre 0 e "
                            + tipo.getValorMaximo() + " " + tipo.getUnidade() + ".");
        }
    }
}