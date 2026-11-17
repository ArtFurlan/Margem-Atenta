package margematenta.dominio;

public class SensorNivelCorrego implements Sensor {

    private final boolean funcionando;

    public SensorNivelCorrego(boolean funcionando) {
        this.funcionando = funcionando;
    }

    @Override
    public double lerValor() {
        return Math.random() * retornarTipo().getValorMaximo();
    }

    @Override
    public boolean verificarFuncionamento() {
        return funcionando;
    }

    @Override
    public TipoSensor retornarTipo() {
        return TipoSensor.NIVEL_CORREGO;
    }
}