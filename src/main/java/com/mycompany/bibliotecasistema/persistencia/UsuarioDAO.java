package com.mycompany.bibliotecasistema.persistencia;

import com.mycompany.bibliotecasistema.modelo.Aluno;
import com.mycompany.bibliotecasistema.modelo.Professor;
import com.mycompany.bibliotecasistema.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public boolean inserir(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, matricula, tipo, curso, departamento) VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT (matricula) DO NOTHING";
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getMatricula());
            if (usuario instanceof Aluno aluno) {
                ps.setString(3, "ALUNO");
                ps.setString(4, aluno.getCurso());
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else if (usuario instanceof Professor professor) {
                ps.setString(3, "PROFESSOR");
                ps.setNull(4, java.sql.Types.VARCHAR);
                ps.setString(5, professor.getDepartamento());
            } else {
                throw new IllegalArgumentException("Tipo de usuário não suportado");
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir usuário: " + e.getMessage(), e);
        }
    }

    public List<Usuario> listar() {
        String sql = "SELECT nome, matricula, tipo, curso, departamento FROM usuarios ORDER BY id";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(montar(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        }
        return usuarios;
    }

    static Usuario montar(ResultSet rs) throws SQLException {
        String nome = rs.getString("nome");
        String matricula = rs.getString("matricula");
        if ("ALUNO".equals(rs.getString("tipo"))) {
            return new Aluno(nome, matricula, rs.getString("curso"));
        }
        return new Professor(nome, matricula, rs.getString("departamento"));
    }

    public boolean excluir(Usuario usuario) {
        String sql = "DELETE FROM usuarios WHERE matricula = ?";
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, usuario.getMatricula());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir usuário: " + e.getMessage(), e);
        }
    }
}
