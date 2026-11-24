package com.mycompany.bibliotecasistema.modelo;

public class Professor extends Usuario {

    public static final int LIMITE_EMPRESTIMOS = 5;
    public static final int PRAZO_DIAS = 14;

    private String departamento;

    public Professor() {
    }

    public Professor(String nome, String matricula, String departamento) {
        super(nome, matricula);
        this.departamento = departamento;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    @Override
    public String getTipo() {
        return "Professor";
    }

    @Override
    public String getVinculo() {
        return departamento;
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
        System.out.println("Departamento: " + departamento);
    }
}
