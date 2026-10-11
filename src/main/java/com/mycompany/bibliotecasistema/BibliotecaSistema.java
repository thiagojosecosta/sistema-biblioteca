package com.mycompany.bibliotecasistema;

import com.mycompany.bibliotecasistema.controle.BibliotecaController;
import com.mycompany.bibliotecasistema.modelo.Biblioteca;
import com.mycompany.bibliotecasistema.visao.BibliotecaView;

public class BibliotecaSistema {

    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        BibliotecaView view = new BibliotecaView();
        BibliotecaController controle = new BibliotecaController(biblioteca, view);

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
