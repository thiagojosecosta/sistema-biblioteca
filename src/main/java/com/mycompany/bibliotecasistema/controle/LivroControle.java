package com.mycompany.bibliotecasistema.controle;

import com.mycompany.bibliotecasistema.modelo.Biblioteca;
import com.mycompany.bibliotecasistema.modelo.Livro;
import com.mycompany.bibliotecasistema.visao.DialogoLivro;
import com.mycompany.bibliotecasistema.visao.PainelLivros;
import com.mycompany.bibliotecasistema.visao.TelaPrincipal;
import java.util.List;

public class LivroControle extends ControleBase {

    private final PainelLivros painel;

    public LivroControle(Biblioteca biblioteca, TelaPrincipal tela, EmprestimoControle emprestimos,
            Runnable atualizarTudo) {
        super(biblioteca, tela, atualizarTudo);
        this.painel = tela.getPainelLivros();
        painel.aoNovo(() -> abrirFormulario(null));
        painel.aoEditar(this::abrirFormulario);
        painel.aoExcluir(this::excluir);
        painel.aoEmprestar(livro -> emprestimos.novoEmprestimo(livro, null));
    }

    public void mostrar(List<Livro> livros) {
        painel.mostrar(livros);
    }

    private void abrirFormulario(Livro existente) {
        DialogoLivro dialogo = new DialogoLivro(tela, existente, biblioteca::salvarLivro);
        if (dialogo.exibir()) {
            String titulo = dialogo.getResultado().getTitulo();
            tela.informar(existente == null ? "Livro “" + titulo + "” cadastrado." : "Livro “" + titulo + "” salvo.");
            atualizarTudo.run();
        }
    }

    private void excluir(Livro livro) {
        boolean confirmado = tela.confirmar("Excluir livro",
                "Excluir o livro “" + livro.getTitulo() + "”?\nEssa ação não pode ser desfeita.",
                "Excluir", true);
        if (confirmado && executar(() -> biblioteca.excluirLivro(livro))) {
            tela.informar("Livro “" + livro.getTitulo() + "” excluído.");
            atualizarTudo.run();
        }
    }
}
