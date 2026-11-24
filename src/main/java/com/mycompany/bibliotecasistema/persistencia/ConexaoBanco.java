package com.mycompany.bibliotecasistema.persistencia;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    private static final Dotenv DOTENV = Dotenv.configure().ignoreIfMissing().load();

    private static final String URL = valor("BIBLIOTECA_DB_URL", "jdbc:postgresql://localhost:5432/biblioteca");
    private static final String USUARIO = valor("BIBLIOTECA_DB_USUARIO", "biblioteca_user");

    /** Código do PostgreSQL para valor repetido em coluna UNIQUE. */
    static final String VIOLACAO_UNICA = "23505";
    /** Código do PostgreSQL para registro ainda usado por outra tabela. */
    static final String VIOLACAO_CHAVE_ESTRANGEIRA = "23503";

    private ConexaoBanco() {
    }

    public static Connection obterConexao() throws SQLException {
        String senha = DOTENV.get("BIBLIOTECA_DB_SENHA");
        if (senha == null || senha.isEmpty()) {
            throw new SQLException("defina BIBLIOTECA_DB_SENHA no arquivo .env ou nas variáveis de ambiente");
        }
        return DriverManager.getConnection(URL, USUARIO, senha);
    }

    /** Erro de banco com uma mensagem que o usuário final entende. */
    static RuntimeException falha(String acao, SQLException e) {
        return new RuntimeException("Não foi possível " + acao + ".\n"
                + "Confira se o banco de dados está ligado e tente de novo (F5).\n\n"
                + "Detalhe: " + e.getMessage(), e);
    }

    private static String valor(String variavel, String padrao) {
        String valor = DOTENV.get(variavel);
        return valor == null || valor.isEmpty() ? padrao : valor;
    }
}
