package br.com.softbox.atest_bdd.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alexandermiro on 11/12/2015.
 */
public class FardamentoFutebol {
    public enum TeamType {FUTSAL, CAMPO};
    private String _teamName;
    private TeamType _teamType;
    private List<FardamentoFutebolJogador> _players = new ArrayList<>();

    public void initFutsal(String teamName) {
        _teamName = teamName;
        _teamType = TeamType.FUTSAL;

        _players.add(new FardamentoFutebolJogador(100, "JOAO A", FardamentoFutebolJogador.JogadorType.GOLEIRO));
        _players.add(new FardamentoFutebolJogador(2, "MANE B", FardamentoFutebolJogador.JogadorType.JOGADOR));
        _players.add(new FardamentoFutebolJogador(4, "MANE C", FardamentoFutebolJogador.JogadorType.JOGADOR));
        _players.add(new FardamentoFutebolJogador(6, "MANE D", FardamentoFutebolJogador.JogadorType.JOGADOR));
        _players.add(new FardamentoFutebolJogador(8, "MANE E", FardamentoFutebolJogador.JogadorType.JOGADOR));
    }

    public String getTeamName() {
        return _teamName;
    }

    public TeamType getTeamType() {
        return _teamType;
    }

    public List<FardamentoFutebolJogador> getPlayers() {
        return _players;
    }
}
