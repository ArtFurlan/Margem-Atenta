package margematenta.dominio;

import java.util.List;
import margematenta.excecao.DadosInvalidosException;

public abstract class EstrategiaPrevisao {

    private final String nome;
    private final int minimoLeituras;

    protected EstrategiaPrevisao(String nome, int minimoLeituras) {
        this.nome = nome;
        this.minimoLeituras = minimoLeituras;
    }

    public String getNome() {
        return nome;
    }

    protected void validarHistorico(List<Medicao> historico) {
        if (historico.size() < minimoLeituras) {
            throw new DadosInvalidosException(
                    "São necessárias pelo menos " + minimoLeituras
                            + " leituras deste sensor. Disponíveis: "
                            + historico.size() + ".");
        }
    }

    protected double calcularMedia(List<Medicao> historico, int inicio, int fim) {
        double soma = 0;

        for (int indice = inicio; indice < fim; indice++) {
            soma += historico.get(indice).valor();
        }

        return soma / (fim - inicio);
    }

    public abstract double prever(List<Medicao> historico);
}