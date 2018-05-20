package model;

import java.io.Serializable;

import util.Status;

/**
 * Created by Andressa on 13/05/2018.
 */

public class Usuario implements Serializable {

    int idUsuario;
    String nome;
    String dataNascimento;
    String idioma;
    String ocupacao;
    String email;
    String senha;
    String localizacao;
    Status statusConta;

    public Usuario() {

    }

    public Usuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuario(int idUsuario, String nome, String dataNascimento, String idioma, String ocupacao,
                   String email, String senha, String localizacao, Status statusConta) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.idioma = idioma;
        this.ocupacao = ocupacao;
        this.email = email;
        this.senha = senha;
        this.localizacao = localizacao;
        this.statusConta = statusConta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(String ocupacao) {
        this.ocupacao = ocupacao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Status getStatusConta() {
        return statusConta;
    }

    public void setStatusConta(Status statusConta) {
        this.statusConta = statusConta;
    }

    @Override
    public String toString() {
        return "Usuario{" + "idUsuario=" + idUsuario + ", nome=" + nome + ", dataNascimento=" + dataNascimento + ", idioma=" + idioma + ", ocupacao=" + ocupacao + ", email=" + email + ", senha=" + senha + ", localizacao=" + localizacao + ", statusConta=" + statusConta + '}';
    }

}
