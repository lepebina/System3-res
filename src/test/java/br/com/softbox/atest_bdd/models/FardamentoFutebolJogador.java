package br.com.softbox.atest_bdd.models;

/**
 * Created by alexandermiro on 14/12/2015.
 */
public class FardamentoFutebolJogador {
    public enum JogadorType {GOLEIRO, JOGADOR};
    public int numero;
    public String nome;
    public JogadorType tipo;

    public FardamentoFutebolJogador(int numero, String nome, JogadorType tipo) {
        this.numero = numero;
        this.nome = nome;
        this.tipo = tipo;
    }

    public FardamentoFutebolJogador(FardamentoFutebolJogador other) {
        numero = other.numero;
        nome = other.nome;
        tipo = other.tipo;
    }
}
