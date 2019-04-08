package br.com.softbox.atest_bdd.models;

import br.com.softbox.utils.CpfCnpjGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class PessoaJuridica {

    private String _razaoSocial;
    private String _nomeFantasia;
    private String _cnpj;
    private String _cep;
    private String _cepCampo1;
    private String _cepCampo2;
    private String _tipoEnd;
    private String _logradouro;
    private String _numero;
    private String _telFixo;
    private String _telFixoDDD;
    private String _telFixoPrefixo;
    private String _telFixoSufixo;
    private String _telCel;
    private String _telCelDDD;
    private String _telCelPrefixo;
    private String _telCelSufixo;
    private String _email;
    private String _senha;

    public PessoaJuridica() {
    }

    public void create(String razaoSoc, String nomeFant) {
        _razaoSocial = razaoSoc;
        _nomeFantasia = nomeFant;
        setCNPJ();
        setEmail();
        setCEP("38400-696");
        setNumero("1001");
        setTelFixo("(34)3350-1001");
        setTelCel("(34) 98754 -2002");
        setSenha("abC 123%");
    }

    public void create(String razaoSoc, String nomeFant, String email) {
        _razaoSocial = razaoSoc;
        _nomeFantasia = nomeFant;
        _email = email;
        setCNPJ();
        setCEP("38400-696");
        setNumero("1001");
        setTelFixo("(34)3350-1001");
        setTelCel("(34) 98754 -2002");
        setSenha("abC 123%"); }

    private void setCNPJ() {
        _cnpj = CpfCnpjGenerator.cnpj();
    }

    public String getCNPJAsRaw() {
        return _cnpj;
    }

    public String getInscricaoEstatual() {
        return "1152082535440";
    }

    public String getCNPJAsPresentation() {
        String cnpj = _cnpj.substring(0, 2)
                + "."
                + _cnpj.substring(2, 5)
                + "."
                + _cnpj.substring(5, 8)
                + "/"
                + _cnpj.substring(8, 12)
                + "-"
                + _cnpj.substring(12);
        return cnpj;
    }

    private void setEmail() {
        _email = _razaoSocial.toLowerCase()
                + "."
                + _nomeFantasia.replaceAll(" ", ".").toLowerCase()
                + "."
                + _cnpj
                + "@qa2.com";
    }

    public String dumpData() {
        String dump = "\n"
                + "\t\tRazao Social.: " + _razaoSocial + "\n"
                + "\t\tNome Fantasia: " + _nomeFantasia + "\n"
                + "\t\tCNPJ.........: " + getCNPJAsPresentation() + "\n"
                + "\t\tEmail........: " + _email + "\n"
                + "\t\tSenha........: " + _senha + "\n"
                + "\t\tCEP..........: " + _cep + "\n"
                + "\t\tTelefone.....: " + _telFixo + "\n"
                + "\t\tCelular......: " + _telCel + "\n";
        return dump;
    }

    public void setRazaoSocial(String newName) {
        _razaoSocial = newName;
    }

    public String getRazaoSocial() {
        return _razaoSocial;
    }

    public void setNomeFantasia(String sobrenome) {
        _nomeFantasia = sobrenome;
    }

    public String getNomeFantasia() {
        return _nomeFantasia;
    }


    public String getCNPJ() {
        return _cnpj;
    }

    public void setCEP(String cep) {
        Pattern pattern = Pattern.compile("(?<campo1>\\d{5})-(?<campo2>\\d{3})");
        Matcher matcher = pattern.matcher(cep);
        if (matcher.find()) {
            _cepCampo1 = matcher.group("campo1");
            _cepCampo2 = matcher.group("campo2");
            _cep = cep;
        }
    }

    public String getCEP() {
        return _cep;
    }

    public String getCEPCampo1() {
        return _cepCampo1;
    }

    public String getCEPCampo2() {
        return _cepCampo2;
    }

    public String getTipoEnd() {
        return _tipoEnd;
    }

    public void setTipoEnd(String _tipoEnd) {
        this._tipoEnd = _tipoEnd;
    }

    public String getLogradouro() {
        return _logradouro;
    }

    public void setLogradouro(String logradouro) {
        _logradouro = logradouro;
    }

    public String getNumero() {
        return _numero;
    }

    public void setNumero(String numero) {
        _numero = numero;
    }

    public String getTelFixoPresentation() {
        return _telFixo;
    }

    public String getTelFixoDDD() {
        return _telFixoDDD;
    }

    public String getTelFixoPrefixo() {
        return _telFixoPrefixo;
    }

    public String getTelFixoSufixo() {
        return _telFixoSufixo;
    }

    public String getTellFixo() {
        return _telFixoPrefixo + "" + _telFixoSufixo;
    }

    public void setTelFixo(String fixo) {
        final String newFixo = fixo.replaceAll(" ", "");
        Pattern pattern = Pattern.compile("\\((?<ddd>\\d{2})\\)(?<tel1>\\d{4,5})-(?<tel2>\\d{4})");
        Matcher matcher = pattern.matcher(newFixo);
        if (matcher.find()) {
            _telFixoDDD = matcher.group("ddd");
            _telFixoPrefixo = matcher.group("tel1");
            _telFixoSufixo = matcher.group("tel2");
            _telFixo = newFixo;
        }
    }

    public String getTelCel() {
        return _telCelPrefixo + "-" + _telCelSufixo;
    }

    public String getTelCellFull() {
        return _telCel;
    }

    public void setTelCel(String telCel) {
        final String newCel = telCel.replaceAll(" ", "");
        Pattern pattern = Pattern.compile("\\((?<ddd>\\d{2})\\)(?<tel1>\\d{4,5})-(?<tel2>\\d{4})");
        Matcher matcher = pattern.matcher(newCel);
        if (matcher.find()) {
            _telCelDDD = matcher.group("ddd");
            _telCelPrefixo = matcher.group("tel1");
            _telCelSufixo = matcher.group("tel2");
            _telCel = newCel;
        }
    }

    public String getTelCellDD() {
        return _telCelDDD;
    }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        _email = email;
    }

    public String getSenha() {
        return _senha;
    }

    public void setSenha(String senha) {
        _senha = senha;
    }


}

