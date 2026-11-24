package com.mycompany.bibliotecasistema.persistencia;

import com.mycompany.bibliotecasistema.modelo.Emprestimo;
import com.mycompany.bibliotecasistema.modelo.RegraNegocioException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

    private static final String SELECT = """
            SELECT e.id, e.data_emprestimo, e.data_devolucao_prevista, e.data_devolucao,
                   l.id AS livro_id, l.titulo, l.autor, l.ano, l.disponivel,
                   u.id AS usuario_id, u.nome, u.matricula, u.tipo, u.curso, u.departamento
              FROM emprestimos e
              JOIN livros l ON l.id = e.id_livro
              JOIN usuarios u ON u.id = e.id_usuario
            """;

    /** Todos os empréstimos: primeiro os em aberto (mais urgentes no topo), depois os devolvidos. */
    public List<Emprestimo> listar() {
        return consultar(SELECT + " ORDER BY e.data_devolucao IS NOT NULL, e.data_devolucao_prevista, e.id DESC", null);
    }

    public List<Emprestimo> listarAbertosDoUsuario(int idUsuario) {
        return consultar(SELECT + " WHERE e.id_usuario = ? AND e.data_devolucao IS NULL", idUsuario);
    }

    private List<Emprestimo> consultar(String sql, Integer parametro) {
        List<Emprestimo> emprestimos = new ArrayList<>();
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    emprestimos.add(montar(rs));
                }
            }
        } catch (SQLException e) {
            throw ConexaoBanco.falha("carregar os empréstimos", e);
        }
        return emprestimos;
    }

    private static Emprestimo montar(ResultSet rs) throws SQLException {
        Emprestimo emprestimo = new Emprestimo(
                LivroDAO.montar(rs, "livro_id"),
                UsuarioDAO.montar(rs, "usuario_id"),
                rs.getObject("data_emprestimo", LocalDate.class),
                rs.getObject("data_devolucao_prevista", LocalDate.class));
        emprestimo.setId(rs.getInt("id"));
        emprestimo.setDataDevolucao(rs.getObject("data_devolucao", LocalDate.class));
        return emprestimo;
    }

    /** Registra o empréstimo e marca o livro como indisponível, tudo ou nada. */
    public void registrar(Emprestimo emprestimo) {
        try (Connection conexao = ConexaoBanco.obterConexao()) {
            conexao.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conexao.prepareStatement(
                        "UPDATE livros SET disponivel = FALSE WHERE id = ? AND disponivel")) {
                    ps.setInt(1, emprestimo.getLivro().getId());
                    if (ps.executeUpdate() == 0) {
                        throw new RegraNegocioException("“" + emprestimo.getLivro().getTitulo()
                                + "” acabou de ser emprestado. Atualize a tela (F5).");
                    }
                }
                try (PreparedStatement ps = conexao.prepareStatement(
                        "INSERT INTO emprestimos (id_livro, id_usuario, data_emprestimo, data_devolucao_prevista) "
                        + "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, emprestimo.getLivro().getId());
                    ps.setInt(2, emprestimo.getUsuario().getId());
                    ps.setObject(3, emprestimo.getDataEmprestimo());
                    ps.setObject(4, emprestimo.getDataDevolucaoPrevista());
                    ps.executeUpdate();
                    try (ResultSet chaves = ps.getGeneratedKeys()) {
                        if (chaves.next()) {
                            emprestimo.setId(chaves.getInt(1));
                        }
                    }
                }
                conexao.commit();
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw ConexaoBanco.falha("registrar o empréstimo", e);
        }
    }

    /** Registra a devolução e marca o livro como disponível, tudo ou nada. */
    public void registrarDevolucao(Emprestimo emprestimo, LocalDate data) {
        try (Connection conexao = ConexaoBanco.obterConexao()) {
            conexao.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conexao.prepareStatement(
                        "UPDATE emprestimos SET data_devolucao = ? WHERE id = ? AND data_devolucao IS NULL")) {
                    ps.setObject(1, data);
                    ps.setInt(2, emprestimo.getId());
                    if (ps.executeUpdate() == 0) {
                        throw new RegraNegocioException("Esse empréstimo já foi devolvido. Atualize a tela (F5).");
                    }
                }
                try (PreparedStatement ps = conexao.prepareStatement(
                        "UPDATE livros SET disponivel = TRUE WHERE id = ?")) {
                    ps.setInt(1, emprestimo.getLivro().getId());
                    ps.executeUpdate();
                }
                conexao.commit();
                emprestimo.setDataDevolucao(data);
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw ConexaoBanco.falha("registrar a devolução", e);
        }
    }
}
