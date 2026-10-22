package com.mycompany.bibliotecasistema.modelo;

import com.mycompany.bibliotecasistema.persistencia.LivroDAO;
import com.mycompany.bibliotecasistema.persistencia.UsuarioDAO;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca {

    private final LivroDAO livroDAO;
    private final UsuarioDAO usuarioDAO;
    private final List<Livro> acervo;
    private final List<Usuario> usuarios;

    public Biblioteca(LivroDAO livroDAO, UsuarioDAO usuarioDAO) {
        this.livroDAO = livroDAO;
        this.usuarioDAO = usuarioDAO;
        this.acervo = new ArrayList<>(livroDAO.listar());
        this.usuarios = new ArrayList<>(usuarioDAO.listar());
    }

    public boolean adicionarLivro(Livro livro) {
        if (!livroDAO.inserir(livro)) {
            return false;
        }
        acervo.add(livro);
        return true;
    }

    public boolean removerLivro(Livro livro) {
        if (!livroDAO.excluir(livro)) {
            return false;
        }
        return acervo.remove(livro);
    }

    public boolean adicionarUsuario(Usuario usuario) {
        if (!usuarioDAO.inserir(usuario)) {
            return false;
        }
        usuarios.add(usuario);
        return true;
    }

    public boolean removerUsuario(Usuario usuario) {
        if (!usuarioDAO.excluir(usuario)) {
            return false;
        }
        return usuarios.remove(usuario);
    }

    public Livro buscarPorTitulo(String titulo) {
        for (Livro l : acervo) {
            if (l.getTitulo().equalsIgnoreCase(titulo)) {
                return l;
            }
        }
        return null;
    }

    public List<Livro> getAcervo() {
        return acervo;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}
