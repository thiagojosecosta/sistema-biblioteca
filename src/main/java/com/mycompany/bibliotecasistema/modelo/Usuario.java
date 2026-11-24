package com.mycompany.bibliotecasistema.modelo;

public abstract class Usuario {

    private int id;
    private String nome;
    private String matricula;

    public Usuario() {
    }

    public Usuario(String nome, String matricula) {
        this.nome = nome;
        this.matricula = matricula;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    /** "Aluno" ou "Professor". */
    public abstract String getTipo();

    /** Curso do aluno ou departamento do professor. */
    public abstract String getVinculo();

    /** Quantos livros o usuário pode ter emprestados ao mesmo tempo. */
    public abstract int getLimiteEmprestimos();

    /** Quantos dias o usuário tem para devolver cada livro. */
    public abstract int getPrazoDias();

    public void exibirDados() {
        System.out.println("Nome: " + nome);
        System.out.println("Matrícula: " + matricula);
    }

    @Override
    public String toString() {
        return nome + " (" + matricula + ")";
    }
}
