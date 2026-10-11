package com.mycompany.bibliotecasistema.visao;

import com.mycompany.bibliotecasistema.modelo.Livro;
import com.mycompany.bibliotecasistema.modelo.Usuario;

public class BibliotecaView {

    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    public void exibirErro(String mensagem) {
        System.err.println("ERRO: " + mensagem);
    }

    public void exibirTitulo(String titulo) {
        System.out.println();
        System.out.println("=== " + titulo + " ===");
    }

    public void exibirLivro(Livro livro) {
        String situacao = livro.isDisponivel() ? "disponível" : "emprestado";
        System.out.println(livro + " [" + situacao + "]");
    }

    public void exibirUsuario(Usuario usuario) {
        usuario.exibirDados();
    }
}
