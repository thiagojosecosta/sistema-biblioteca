package com.mycompany.bibliotecasistema.modelo;

public class Aluno extends Usuario {

    private String curso;

    public Aluno() {
    }

    public Aluno(String nome, String matricula, String curso) {
        super(nome, matricula);
        this.curso = curso;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    @Override
    public void exibirDados() {
        super.exibirDados();
        System.out.println("Curso: " + curso);
    }
}
