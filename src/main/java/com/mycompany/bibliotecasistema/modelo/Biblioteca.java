package com.mycompany.bibliotecasistema.modelo;

import com.mycompany.bibliotecasistema.persistencia.EmprestimoDAO;
import com.mycompany.bibliotecasistema.persistencia.LivroDAO;
import com.mycompany.bibliotecasistema.persistencia.UsuarioDAO;
import java.time.LocalDate;
import java.util.List;

/**
 * Regras de negócio da biblioteca. O banco de dados é a fonte dos dados;
 * esta classe decide o que pode ou não ser feito.
 */
public class Biblioteca {

    private final LivroDAO livroDAO;
    private final UsuarioDAO usuarioDAO;
    private final EmprestimoDAO emprestimoDAO;

    public Biblioteca(LivroDAO livroDAO, UsuarioDAO usuarioDAO, EmprestimoDAO emprestimoDAO) {
        this.livroDAO = livroDAO;
        this.usuarioDAO = usuarioDAO;
        this.emprestimoDAO = emprestimoDAO;
    }

    public LocalDate hoje() {
        return LocalDate.now();
    }

    // ---------- Livros ----------

    public List<Livro> listarLivros() {
        return livroDAO.listar();
    }

    public List<Livro> listarLivrosDisponiveis() {
        return livroDAO.listar().stream().filter(Livro::isDisponivel).toList();
    }

    public void salvarLivro(Livro livro) {
        if (livro.getId() == 0) {
            if (!livroDAO.inserir(livro)) {
                throw new RegraNegocioException("Já existe um livro com esse título e autor.");
            }
        } else {
            livroDAO.atualizar(livro);
        }
    }

    public void excluirLivro(Livro livro) {
        if (!livro.isDisponivel()) {
            throw new RegraNegocioException("“" + livro.getTitulo()
                    + "” está emprestado. Registre a devolução antes de excluir.");
        }
        livroDAO.excluir(livro);
    }

    // ---------- Usuários ----------

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listar();
    }

    public void salvarUsuario(Usuario usuario) {
        if (usuario.getId() == 0) {
            if (!usuarioDAO.inserir(usuario)) {
                throw new RegraNegocioException("Já existe um usuário com a matrícula " + usuario.getMatricula() + ".");
            }
        } else {
            usuarioDAO.atualizar(usuario);
        }
    }

    public void excluirUsuario(Usuario usuario) {
        if (!emprestimoDAO.listarAbertosDoUsuario(usuario.getId()).isEmpty()) {
            throw new RegraNegocioException(usuario.getNome()
                    + " está com livros emprestados. Registre as devoluções antes de excluir.");
        }
        usuarioDAO.excluir(usuario);
    }

    // ---------- Empréstimos ----------

    public List<Emprestimo> listarEmprestimos() {
        return emprestimoDAO.listar();
    }

    public LocalDate calcularDevolucao(Usuario usuario) {
        return hoje().plusDays(usuario.getPrazoDias());
    }

    /**
     * Diz por que o usuário não pode pegar outro livro agora, ou devolve
     * {@code null} se ele pode.
     */
    public String motivoBloqueio(Usuario usuario) {
        List<Emprestimo> abertos = emprestimoDAO.listarAbertosDoUsuario(usuario.getId());
        LocalDate hoje = hoje();
        if (abertos.stream().anyMatch(e -> e.isAtrasado(hoje))) {
            return usuario.getNome() + " tem livro com devolução atrasada. "
                    + "Registre a devolução antes de um novo empréstimo.";
        }
        if (abertos.size() >= usuario.getLimiteEmprestimos()) {
            return usuario.getNome() + " já atingiu o limite de " + usuario.getLimiteEmprestimos()
                    + " livros emprestados (" + usuario.getTipo().toLowerCase() + ").";
        }
        return null;
    }

    public Emprestimo emprestar(Livro livro, Usuario usuario) {
        String motivo = motivoBloqueio(usuario);
        if (motivo != null) {
            throw new RegraNegocioException(motivo);
        }
        if (!livro.isDisponivel()) {
            throw new RegraNegocioException("“" + livro.getTitulo() + "” já está emprestado.");
        }
        Emprestimo emprestimo = new Emprestimo(livro, usuario, hoje(), calcularDevolucao(usuario));
        emprestimoDAO.registrar(emprestimo);
        livro.setDisponivel(false);
        return emprestimo;
    }

    public void devolver(Emprestimo emprestimo) {
        if (!emprestimo.isAberto()) {
            throw new RegraNegocioException("Esse empréstimo já foi devolvido.");
        }
        emprestimoDAO.registrarDevolucao(emprestimo, hoje());
    }
}
