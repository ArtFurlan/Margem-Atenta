package margematenta.dominio;

import java.time.Instant;

public record Alerta(
        TipoSensor tipo,
        NivelAlerta nivel,
        double valorPrevisto,
        double limiarAtingido,
        String estrategia,
        Instant dataHora
) {
}