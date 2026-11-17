package margematenta.dominio;

public interface Sensor {

    double lerValor();

    boolean verificarFuncionamento();

    TipoSensor retornarTipo();
}