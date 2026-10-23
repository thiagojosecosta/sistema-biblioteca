package com.mycompany.bibliotecasistema.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    private static final String URL = valor("BIBLIOTECA_DB_URL", "jdbc:postgresql://localhost:5432/biblioteca");
    private static final String USUARIO = valor("BIBLIOTECA_DB_USUARIO", "biblioteca_user");
    private static final String SENHA = obrigatorio("BIBLIOTECA_DB_SENHA");

    private ConexaoBanco() {
    }

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    private static String obrigatorio(String variavel) {
        String valor = System.getenv(variavel);
        if (valor == null || valor.isEmpty()) {
            throw new IllegalStateException("Defina a variável de ambiente " + variavel);
        }
        return valor;
    }

    private static String valor(String variavel, String padrao) {
        String valor = System.getenv(variavel);
        return valor == null || valor.isEmpty() ? padrao : valor;
    }
}
