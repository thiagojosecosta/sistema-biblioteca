package com.mycompany.bibliotecasistema.modelo;

import java.util.ArrayList;
import java.util.List;

public class Biblioteca {

    private List<Livro> acervo = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();

    public void adicionarLivro(Livro livro) {
        acervo.add(livro);
    }

    public void removerLivro(Livro livro) {
        acervo.remove(livro);
    }

    public void adicionarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public void removerUsuario(Usuario usuario) {
        usuarios.remove(usuario);
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
