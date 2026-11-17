package margematenta.dominio;

public enum NivelAlerta {

    ATENCAO("Atenção"),
    ALERTA("Alerta"),
    CRITICO("Crítico");

    private final String descricao;

    NivelAlerta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}