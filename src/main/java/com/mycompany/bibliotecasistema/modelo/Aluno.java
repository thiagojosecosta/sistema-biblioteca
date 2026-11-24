package com.mycompany.bibliotecasistema.modelo;

public class Aluno extends Usuario {

    public static final int LIMITE_EMPRESTIMOS = 3;
    public static final int PRAZO_DIAS = 7;

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
    public String getTipo() {
        return "Aluno";
    }

    @Override
    public String getVinculo() {
        return curso;
    }

    @Override
    public int getLimiteEmprestimos() {
        return LIMITE_EMPRESTIMOS;
    }

    @Override
    public int getPrazoDias() {
        return PRAZO_DIAS;
    }

    @Override
    public void exibirDados() {
        super.exibirDados();
        System.out.println("Curso: " + curso);
    }
}
