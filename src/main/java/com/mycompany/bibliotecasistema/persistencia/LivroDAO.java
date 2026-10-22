package com.mycompany.bibliotecasistema.persistencia;

import com.mycompany.bibliotecasistema.modelo.Livro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    public boolean inserir(Livro livro) {
        String sql = "INSERT INTO livros (titulo, autor, ano, disponivel) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT (titulo, autor) DO NOTHING";
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, livro.getTitulo());
            ps.setString(2, livro.getAutor());
            ps.setInt(3, livro.getAno());
            ps.setBoolean(4, livro.isDisponivel());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir livro: " + e.getMessage(), e);
        }
    }

    public List<Livro> listar() {
        String sql = "SELECT titulo, autor, ano, disponivel FROM livros ORDER BY id";
        List<Livro> livros = new ArrayList<>();
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                livros.add(montar(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar livros: " + e.getMessage(), e);
        }
        return livros;
    }

    static Livro montar(ResultSet rs) throws SQLException {
        Livro livro = new Livro(rs.getString("titulo"), rs.getString("autor"), rs.getInt("ano"));
        livro.setDisponivel(rs.getBoolean("disponivel"));
        return livro;
    }

    public boolean excluir(Livro livro) {
        String sql = "DELETE FROM livros WHERE titulo = ? AND autor = ?";
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, livro.getTitulo());
            ps.setString(2, livro.getAutor());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir livro: " + e.getMessage(), e);
        }
    }
}
