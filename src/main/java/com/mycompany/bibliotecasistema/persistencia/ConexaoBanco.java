package com.mycompany.bibliotecasistema.persistencia;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();

    private static final String URL = valor("BIBLIOTECA_DB_URL", "jdbc:postgresql://localhost:5432/biblioteca");
    private static final String USUARIO = valor("BIBLIOTECA_DB_USUARIO", "biblioteca_user");
    private static final String SENHA = obrigatorio("BIBLIOTECA_DB_SENHA");

    private ConexaoBanco() {
    }

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    private static String obrigatorio(String variavel) {
        String valor = DOTENV.get(variavel);
        if (valor == null || valor.isEmpty()) {
            throw new IllegalStateException("Defina " + variavel + " no arquivo .env ou nas variáveis de ambiente");
        }
        return valor;
    }

    private static String valor(String variavel, String padrao) {
        String valor = DOTENV.get(variavel);
        return valor == null || valor.isEmpty() ? padrao : valor;
    }
}
