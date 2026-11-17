package margematenta.dominio;

public abstract class ComponenteTerritorial {

    private final String id;
    private final String nome;
    private final String regiao;
    private String status;

    protected ComponenteTerritorial(String id, String nome, String regiao) {
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
        this.status = "Aguardando verificação";
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public String getStatus() {
        return status;
    }

    protected void atualizarStatus(String status) {
        this.status = status;
    }

    public abstract void verificarStatus();
}