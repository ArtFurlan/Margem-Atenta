package margematenta.servico;

import java.util.Map;
import java.util.function.Supplier;
import margematenta.dominio.EstrategiaPrevisao;
import margematenta.dominio.PrevisaoComparacao;
import margematenta.dominio.PrevisaoMediaMovel;

public class FabricaPrevisao {

    public enum Metodo {
        MEDIA_MOVEL,
        COMPARACAO
    }

    private final Map<Metodo, Supplier<EstrategiaPrevisao>> criadores = Map.of(
            Metodo.MEDIA_MOVEL, PrevisaoMediaMovel::new,
            Metodo.COMPARACAO, PrevisaoComparacao::new
    );

    public EstrategiaPrevisao criar(Metodo metodo) {
        return criadores.get(metodo).get();
    }
}