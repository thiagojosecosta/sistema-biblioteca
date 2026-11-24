package com.mycompany.bibliotecasistema.persistencia;

import com.mycompany.bibliotecasistema.modelo.Aluno;
import com.mycompany.bibliotecasistema.modelo.Professor;
import com.mycompany.bibliotecasistema.modelo.RegraNegocioException;
import com.mycompany.bibliotecasistema.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /** Insere o usuário e preenche o id. Devolve false se a matrícula já existe. */
    public boolean inserir(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, matricula, tipo, curso, departamento) VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT (matricula) DO NOTHING";
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getMatricula());
            preencherTipo(ps, 3, usuario);
            if (ps.executeUpdate() == 0) {
                return false;
            }
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    usuario.setId(chaves.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw ConexaoBanco.falha("cadastrar o usuário", e);
        }
    }

    public void atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, matricula = ?, tipo = ?, curso = ?, departamento = ? WHERE id = ?";
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getMatricula());
            preencherTipo(ps, 3, usuario);
            ps.setInt(6, usuario.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (ConexaoBanco.VIOLACAO_UNICA.equals(e.getSQLState())) {
                throw new RegraNegocioException("Já existe um usuário com a matrícula " + usuario.getMatricula() + ".");
            }
            throw ConexaoBanco.falha("salvar o usuário", e);
        }
    }

    /** Preenche tipo, curso e departamento a partir da posição indicada. */
    private static void preencherTipo(PreparedStatement ps, int posicao, Usuario usuario) throws SQLException {
        if (usuario instanceof Aluno aluno) {
            ps.setString(posicao, "ALUNO");
            ps.setString(posicao + 1, aluno.getCurso());
            ps.setNull(posicao + 2, Types.VARCHAR);
        } else if (usuario instanceof Professor professor) {
            ps.setString(posicao, "PROFESSOR");
            ps.setNull(posicao + 1, Types.VARCHAR);
            ps.setString(posicao + 2, professor.getDepartamento());
        } else {
            throw new IllegalArgumentException("Tipo de usuário não suportado");
        }
    }

    public List<Usuario> listar() {
        String sql = "SELECT id, nome, matricula, tipo, curso, departamento FROM usuarios ORDER BY nome";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(montar(rs, "id"));
            }
        } catch (SQLException e) {
            throw ConexaoBanco.falha("carregar os usuários", e);
        }
        return usuarios;
    }

    static Usuario montar(ResultSet rs, String colunaId) throws SQLException {
        String nome = rs.getString("nome");
        String matricula = rs.getString("matricula");
        Usuario usuario = "ALUNO".equals(rs.getString("tipo"))
                ? new Aluno(nome, matricula, rs.getString("curso"))
                : new Professor(nome, matricula, rs.getString("departamento"));
        usuario.setId(rs.getInt(colunaId));
        return usuario;
    }

    public void excluir(Usuario usuario) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conexao = ConexaoBanco.obterConexao();
                PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            if (ConexaoBanco.VIOLACAO_CHAVE_ESTRANGEIRA.equals(e.getSQLState())) {
                throw new RegraNegocioException(usuario.getNome()
                        + " tem histórico de empréstimos e não pode ser excluído.");
            }
            throw ConexaoBanco.falha("excluir o usuário", e);
        }
    }
}
