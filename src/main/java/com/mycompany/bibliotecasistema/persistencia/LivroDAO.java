package com.mycompany.bibliotecasistema.persistencia;

import com.mycompany.bibliotecasistema.modelo.Livro;
import com.mycompany.bibliotecasistema.modelo.RegraNegocioException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    /** Insere o livro e preenche o id. Devolve false se já existe livro com o mesmo título e autor. */
    public boolean inserir(Livro livro) {
        String sql = "INSERT INTO livros (titulo, autor, ano, disponivel) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT (titulo, autor) DO NOTHING";
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, livro.getTitulo());
            ps.setString(2, livro.getAutor());
            ps.setInt(3, livro.getAno());
            ps.setBoolean(4, livro.isDisponivel());
            if (ps.executeUpdate() == 0) {
                return false;
            }
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    livro.setId(chaves.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw ConexaoBanco.falha("cadastrar o livro", e);
        }
    }

    public void atualizar(Livro livro) {
        String sql = "UPDATE livros SET titulo = ?, autor = ?, ano = ? WHERE id = ?";
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, livro.getTitulo());
            ps.setString(2, livro.getAutor());
            ps.setInt(3, livro.getAno());
            ps.setInt(4, livro.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (ConexaoBanco.VIOLACAO_UNICA.equals(e.getSQLState())) {
                throw new RegraNegocioException("Já existe um livro com esse título e autor.");
            }
            throw ConexaoBanco.falha("salvar o livro", e);
        }
    }

    public List<Livro> listar() {
        String sql = "SELECT id, titulo, autor, ano, disponivel FROM livros ORDER BY titulo";
        List<Livro> livros = new ArrayList<>();
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                livros.add(montar(rs, "id"));
            }
        } catch (SQLException e) {
            throw ConexaoBanco.falha("carregar os livros", e);
        }
        return livros;
    }

    static Livro montar(ResultSet rs, String colunaId) throws SQLException {
        Livro livro = new Livro(rs.getString("titulo"), rs.getString("autor"), rs.getInt("ano"));
        livro.setId(rs.getInt(colunaId));
        livro.setDisponivel(rs.getBoolean("disponivel"));
        return livro;
    }

    public void excluir(Livro livro) {
        String sql = "DELETE FROM livros WHERE id = ?";
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, livro.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (ConexaoBanco.VIOLACAO_CHAVE_ESTRANGEIRA.equals(e.getSQLState())) {
                throw new RegraNegocioException("“" + livro.getTitulo()
                        + "” tem histórico de empréstimos e não pode ser excluído.");
            }
            throw ConexaoBanco.falha("excluir o livro", e);
        }
    }
}
