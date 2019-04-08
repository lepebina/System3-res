package br.com.softbox.atest_bdd.models;

import br.com.softbox.utils.CpfCnpjGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class PessoaFisica {



    public enum Sexo { MASCULINO, FEMININO };

    private String _nome;
    private String _sobreNome;
    private Sexo   _sexo;
    private String _nascDDMMYYYY;
    private String _nascDia;
    private String _nascMes;
    private String _nascAno;
    private String _cpf;
    private String _cep;
    private String _cepCampo1;
    private String _cepCampo2;
    private String _tipoEnd;
    private String _logradouro;
    private String _numero;
    private String _complemento;
    private String _bairro;
    private String _cidade;
    private String _estado;
    private String _pontoRef;
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

    public PessoaFisica() {
    }

    public void born(String name, String surname) {
        _nome = name;
        _sobreNome = surname;
        setNascDDMMYYYY("01/01/1990");
        setCPF();
        setEmail();
        setCEP("38400-696");
        setNumero("1");
        setPontoRef("Ao lado do bar do Toin");
        setTelFixo("(34)3131-2021");
        setTelCel("(34) 99991 -2021");
        setSenha("abC 123%");
    }

    public void born(String name, String surname, String email) {
        _nome = name;
        _sobreNome = surname;
        _email = email;
        setNascDDMMYYYY("01/01/1990");
        setCPF();
        setCEP("38400-696");
        setNumero("1");
        setPontoRef("Ao lado do bar do Toin");
        setTelFixo("(34)3131-2021");
        setTelCel("(34) 99991 -2021");
        setSenha("abC 123%");
    }
    private void setCPF() {
        _cpf = CpfCnpjGenerator.cpf();
    }

    public String getCPFasRaw() {
        return _cpf;
    }

    public String getCPFasPresentation() {
        String cpf = _cpf.substring(0, 3) + "." + _cpf.substring(3, 6) + "." + _cpf.substring(6, 9) + "-" + _cpf.substring(9);
        return cpf;
    }

    private void setEmail() {
        _email = _nome.toLowerCase()
               + "."
               + _sobreNome.replaceAll(" ", ".").toLowerCase()
               + "."
               + _cpf
               + "@qa1.com";
    }

    public String dumpData() {
        String dump = "\n"
                    + "\t\tNome......: " + _nome + "\n"
                    + "\t\tSobrenome.: " + _sobreNome + "\n"
                    + "\t\tSexo......: " + ((_sexo == Sexo.FEMININO) ? "F" : "M") + "\n"
                    + "\t\tNascimento: " + _nascDDMMYYYY + "\n"
                    + "\t\tCPF.......: " + getCPFasPresentation() + "\n"
                    + "\t\tEmail.....: " + _email + "\n"
                    + "\t\tSenha.....: " + _senha + "\n"
                    + "\t\tCEP.......: " + _cep + "\n"
                    + "\t\tTelefone..: " + _telFixo + "\n"
                    + "\t\tCelular...: " + _telCel + "\n";
        return dump;
    }

    public void setNome(String newName) {
        _nome = newName;
    }

    public String getNome() {
        return _nome;
    }

    public void setSobrenome(String sobrenome) {
        _sobreNome = sobrenome;
    }

    public String getSobreNome() {
        return _sobreNome;
    }

    public Sexo getSexo() {
        return _sexo;
    }

    public boolean isFemale() {
        return _sexo == Sexo.FEMININO;
    }

    public boolean setSexo(String sexo) {
        boolean retCode = false;
        if (sexo.equalsIgnoreCase("F")) {
            _sexo = Sexo.FEMININO;
        } else {
            _sexo = Sexo.MASCULINO;
        }
        return retCode;
    }

    public String getNascDDMMYYYY() {
        return _nascDDMMYYYY;
    }

    public boolean setNascDDMMYYYY(String nasc) {
        boolean retCode = false;
        Pattern  pattern = Pattern.compile("(?<dia>\\d{1,2})[-/](?<mes>\\d{1,2})[-/](?<ano>\\d{4})");
        Matcher matcher = pattern.matcher(nasc);
        if (matcher.find()) {
            _nascDia = matcher.group("dia");
            if (_nascDia.startsWith("0")) {
                _nascDia = _nascDia.replaceAll("0", "");
            }
            _nascMes = matcher.group("mes");
            if (_nascMes.startsWith("0")) {
                _nascMes = _nascMes.replaceAll("0", "");
            }
            _nascAno = matcher.group("ano");
            _nascDDMMYYYY = nasc;
           retCode = true;
        }
        return retCode;
    }

    public String getNascMesName() {
        final int mounth = Integer.parseInt(_nascMes);
        String m = "";
        switch (mounth) {
            case 1:m = "Jan"; break;
            case 2 :m = "Fev"; break;
            case 3 :m = "Mar"; break;
            case 4 :m = "Abr"; break;
            case 5 :m = "Mai"; break;
            case 6 :m = "Jun"; break;
            case 7 :m = "Jul"; break;
            case 8 :m = "Ago"; break;
            case 9 :m = "Set"; break;
            case 10 :m = "Out"; break;
            case 11 :m = "Nov"; break;
            case 12 :m = "Dez"; break;
            default:
                break;
        }
        return m;
    }


    public String getCPF() {
        return _cpf;
    }

    public boolean setCPF(String cpf) {
        boolean retCode = false;
        Pattern  pattern = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
        Matcher matcher = pattern.matcher(cpf);
        if (matcher.matches()) {
            _cpf = cpf;
           retCode = true;
        }
        return retCode;
    }

    public void setCEP(String cep) {
        Pattern  pattern = Pattern.compile("(?<campo1>\\d{5})-(?<campo2>\\d{3})");
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

    public String getBairro() {
        return _bairro;
    }

    public void setBairro(String bairro) {
        _bairro = bairro;
    }

    public String getCidade() {
        return _cidade;
    }

    public void setCidade(String cidade) {
        cidade = cidade;
    }

    public String getEstado() {
        return _estado;
    }

    public void setEstado(String estado) {
        _estado = estado;
    }

    public String getPontoRef() {
        return _pontoRef;
    }

    public void setPontoRef(String pontoRef) {
        _pontoRef = pontoRef;
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
        Pattern  pattern = Pattern.compile("\\((?<ddd>\\d{2})\\)(?<tel1>\\d{4,5})-(?<tel2>\\d{4})");
        Matcher matcher = pattern.matcher(newFixo);
        if (matcher.find()) {
            _telFixoDDD = matcher.group("ddd");
            _telFixoPrefixo = matcher.group("tel1");
            _telFixoSufixo = matcher.group("tel2");
            _telFixo = newFixo;
        }
    }

    public String getTelCel() {
        return _telCelPrefixo + "" + _telCelSufixo;
    }

    public String getTelCellFull() {
        return _telCel;
    }

    public void setTelCel(String telCel) {
        final String newCel = telCel.replaceAll(" ", "");
        Pattern  pattern = Pattern.compile("\\((?<ddd>\\d{2})\\)(?<tel1>\\d{4,5})-(?<tel2>\\d{4})");
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

    public String getNascDia() {
        return _nascDia;
    }

    public String getNascMes() {
        return _nascMes;
    }

    public String getNascAno() {
        return _nascAno;
    }
}

