package com.mycompany.bibliotecasistema.modelo;

public class Usuario {

    private String nome;
    private String matricula;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    
    public void exibirDados() {
        System.err.println("Nome: " + nome);
        System.err.println("Matrícula:" + matricula);
    }
}
