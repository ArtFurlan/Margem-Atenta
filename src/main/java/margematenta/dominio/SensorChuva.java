package margematenta.dominio;

public class SensorChuva implements Sensor {

    private final boolean funcionando;

    public SensorChuva(boolean funcionando) {
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
        return TipoSensor.CHUVA;
    }
}