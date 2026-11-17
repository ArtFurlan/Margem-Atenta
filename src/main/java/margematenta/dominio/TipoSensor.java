package margematenta.dominio;

public enum TipoSensor {

    CHUVA("Chuva acumulada", "mm", 80),
    NIVEL_CORREGO("Nível do córrego", "m", 2.5),
    UMIDADE_SOLO("Umidade do solo", "%", 100);

    private final String descricao;
    private final String unidade;
    private final double valorMaximo;

    TipoSensor(String descricao, String unidade, double valorMaximo) {
        this.descricao = descricao;
        this.unidade = unidade;
        this.valorMaximo = valorMaximo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUnidade() {
        return unidade;
    }

    public double getValorMaximo() {
        return valorMaximo;
    }

    public boolean aceitaValor(double valor) {
        return Double.isFinite(valor) && valor >= 0 && valor <= valorMaximo;
    }
}