package com.mycompany.bibliotecasistema.modelo;

public class Aluno extends Usuario {

    private String curso;

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    @Override
    public void exibirDados() {
        super.exibirDados();
        System.err.println("Curso: " + curso);
    }
}
