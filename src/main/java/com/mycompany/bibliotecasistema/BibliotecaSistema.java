package com.mycompany.bibliotecasistema;

import com.mycompany.bibliotecasistema.controle.BibliotecaControle;
import com.mycompany.bibliotecasistema.modelo.Biblioteca;
import com.mycompany.bibliotecasistema.persistencia.LivroDAO;
import com.mycompany.bibliotecasistema.persistencia.UsuarioDAO;
import com.mycompany.bibliotecasistema.visao.BibliotecaVisao;

public class BibliotecaSistema {

    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca(new LivroDAO(), new UsuarioDAO());
        BibliotecaVisao visao = new BibliotecaVisao();
        BibliotecaControle controle = new BibliotecaControle(biblioteca, visao);

        controle.cadastrarAluno("Maria Silva", "2024001", "Sistemas de Informação");
        controle.cadastrarProfessor("João Motta", "P0001", "Computação");

        controle.cadastrarLivro("Clean Code", "Robert C. Martin", 2008);
        controle.cadastrarLivro("Dom Casmurro", "Machado de Assis", 1899);
        controle.cadastrarLivro("Use a Cabeça! Padrões de Projeto", "Freeman & Robson", 2009);

        controle.listarUsuarios();
        controle.listarAcervo();

        controle.buscarLivro("Dom Casmurro");
        controle.buscarLivro("Livro Inexistente");
    }
}
