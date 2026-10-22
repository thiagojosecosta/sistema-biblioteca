package com.mycompany.bibliotecasistema.controle;

import com.mycompany.bibliotecasistema.modelo.Aluno;
import com.mycompany.bibliotecasistema.modelo.Biblioteca;
import com.mycompany.bibliotecasistema.modelo.Livro;
import com.mycompany.bibliotecasistema.modelo.Professor;
import com.mycompany.bibliotecasistema.modelo.Usuario;
import com.mycompany.bibliotecasistema.visao.BibliotecaVisao;

public class BibliotecaControle {

    private final Biblioteca biblioteca;
    private final BibliotecaVisao view;

    public BibliotecaControle(Biblioteca biblioteca, BibliotecaVisao view) {
        this.biblioteca = biblioteca;
        this.view = view;
    }

    public void cadastrarLivro(String titulo, String autor, int ano) {
        Livro livro = new Livro(titulo, autor, ano);
        if (biblioteca.adicionarLivro(livro)) {
            view.exibirMensagem("Livro cadastrado: " + livro);
        } else {
            view.exibirMensagem("Livro já cadastrado: " + livro);
        }
    }

    public void cadastrarAluno(String nome, String matricula, String curso) {
        Aluno aluno = new Aluno(nome, matricula, curso);
        if (biblioteca.adicionarUsuario(aluno)) {
            view.exibirMensagem("Aluno cadastrado: " + nome);
        } else {
            view.exibirMensagem("Matrícula já cadastrada: " + matricula);
        }
    }

    public void cadastrarProfessor(String nome, String matricula, String departamento) {
        Professor professor = new Professor(nome, matricula, departamento);
        if (biblioteca.adicionarUsuario(professor)) {
            view.exibirMensagem("Professor cadastrado: " + nome);
        } else {
            view.exibirMensagem("Matrícula já cadastrada: " + matricula);
        }
    }

    public void listarAcervo() {
        view.exibirTitulo("Acervo da biblioteca");
        for (Livro l : biblioteca.getAcervo()) {
            view.exibirLivro(l);
        }
    }

    public void listarUsuarios() {
        view.exibirTitulo("Usuários cadastrados");
        for (Usuario u : biblioteca.getUsuarios()) {
            view.exibirUsuario(u);
            view.exibirMensagem("---");
        }
    }

    public void buscarLivro(String titulo) {
        Livro encontrado = biblioteca.buscarPorTitulo(titulo);
        if (encontrado == null) {
            view.exibirErro("Livro não encontrado: " + titulo);
        } else {
            view.exibirTitulo("Livro encontrado");
            view.exibirLivro(encontrado);
        }
    }
}
