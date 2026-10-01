package com.mycompany.bibliotecasistema.modelo;

public class Professor extends Usuario {

    private String departamento;

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    @Override
    public void exibirDados() {
        super.exibirDados();
        System.err.println("Departamento: " + departamento);
    }
}
