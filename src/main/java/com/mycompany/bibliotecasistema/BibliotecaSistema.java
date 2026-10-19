package com.mycompany.bibliotecasistema;

import com.mycompany.bibliotecasistema.controle.BibliotecaControle;
import com.mycompany.bibliotecasistema.modelo.Biblioteca;
import com.mycompany.bibliotecasistema.visao.BibliotecaVisao;

public class BibliotecaSistema {

    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        BibliotecaVisao view = new BibliotecaVisao();
        BibliotecaControle controller = new BibliotecaControle(biblioteca, view);

        controller.cadastrarAluno("Maria Silva", "2024001", "Sistemas de Informação");
        controller.cadastrarProfessor("João Motta", "P0001", "Computação");

        controller.cadastrarLivro("Clean Code", "Robert C. Martin", 2008);
        controller.cadastrarLivro("Dom Casmurro", "Machado de Assis", 1899);
        controller.cadastrarLivro("Use a Cabeça! Padrões de Projeto", "Freeman & Robson", 2009);

        controller.listarUsuarios();
        controller.listarAcervo();

        controller.buscarLivro("Dom Casmurro");
        controller.buscarLivro("Livro Inexistente");
    }
}
