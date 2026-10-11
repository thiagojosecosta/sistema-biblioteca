package com.mycompany.bibliotecasistema.modelo;

public class Professor extends Usuario {

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
    public void exibirDados() {
        super.exibirDados();
        System.out.println("Departamento: " + departamento);
    }
}
