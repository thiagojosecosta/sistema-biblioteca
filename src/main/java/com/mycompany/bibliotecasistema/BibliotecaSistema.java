package com.mycompany.bibliotecasistema;

import com.mycompany.bibliotecasistema.controle.BibliotecaControle;
import com.mycompany.bibliotecasistema.modelo.Biblioteca;
import com.mycompany.bibliotecasistema.visao.BibliotecaVisao;

public class BibliotecaSistema {

    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        BibliotecaVisao visao = new BibliotecaVisao();
        BibliotecaControle controle = new BibliotecaControle(biblioteca, visao);

        controle.cadastrarLivro("Dom Casmurro", "Machado de Assis", 1899);
        controle.cadastrarLivro("Clean Code", "Robert C. Martin", 2008);
        controle.cadastrarAluno("Maria Silva", "2024001", "Sistemas de Informação");
        controle.cadastrarProfessor("João Souza", "P100", "Computação");

        controle.listarAcervo();
        controle.listarUsuarios();
        controle.buscarLivro("clean code");
        controle.buscarLivro("Inexistente");
    }
}
