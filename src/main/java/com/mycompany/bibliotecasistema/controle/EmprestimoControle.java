package com.mycompany.bibliotecasistema.controle;

import com.mycompany.bibliotecasistema.modelo.Biblioteca;
import com.mycompany.bibliotecasistema.modelo.Emprestimo;
import com.mycompany.bibliotecasistema.modelo.Livro;
import com.mycompany.bibliotecasistema.modelo.Usuario;
import com.mycompany.bibliotecasistema.visao.DialogoEmprestimo;
import com.mycompany.bibliotecasistema.visao.PainelEmprestimos;
import com.mycompany.bibliotecasistema.visao.TelaPrincipal;
import com.mycompany.bibliotecasistema.visao.componentes.Formato;
import java.util.List;

public class EmprestimoControle extends ControleBase {

    private final PainelEmprestimos painel;

    public EmprestimoControle(Biblioteca biblioteca, TelaPrincipal tela, Runnable atualizarTudo) {
        super(biblioteca, tela, atualizarTudo);
        this.painel = tela.getPainelEmprestimos();
        painel.aoNovo(() -> novoEmprestimo(null, null));
        painel.aoDevolver(this::devolver);
    }

    public void mostrar(List<Emprestimo> emprestimos) {
        painel.mostrar(emprestimos);
    }

    /** Abre o formulário de empréstimo, já com o livro e/ou usuário escolhidos se vierem de outra tela. */
    public void novoEmprestimo(Livro livroInicial, Usuario usuarioInicial) {
        List<Usuario> usuarios;
        List<Livro> livros;
        try {
            usuarios = biblioteca.listarUsuarios();
            livros = biblioteca.listarLivrosDisponiveis();
        } catch (RuntimeException e) {
            tela.mostrarErro(e.getMessage());
            return;
        }
        if (usuarios.isEmpty()) {
            tela.mostrarAviso("Cadastre um usuário antes de registrar um empréstimo (menu Usuários).");
            return;
        }
        if (livros.isEmpty()) {
            tela.mostrarAviso("Nenhum livro disponível no momento. Todos estão emprestados.");
            return;
        }
        DialogoEmprestimo dialogo = new DialogoEmprestimo(tela, usuarios, livros, livroInicial, usuarioInicial,
                biblioteca::motivoBloqueio, biblioteca::calcularDevolucao, biblioteca::emprestar);
        if (dialogo.exibir()) {
            Emprestimo e = dialogo.getResultado();
            tela.informar("Empréstimo registrado: “" + e.getLivro().getTitulo() + "” com " + e.getUsuario().getNome()
                    + ", devolver até " + Formato.data(e.getDataDevolucaoPrevista()) + ".");
            atualizarTudo.run();
        }
    }

    public void devolver(Emprestimo emprestimo) {
        if (!emprestimo.isAberto()) {
            return;
        }
        long atraso = emprestimo.getDiasAtraso(biblioteca.hoje());
        String mensagem = "Registrar a devolução de “" + emprestimo.getLivro().getTitulo() + "”, emprestado para "
                + emprestimo.getUsuario().getNome() + "?";
        if (atraso > 0) {
            mensagem += "\n\nAtenção: devolução com " + Formato.plural(atraso, "dia", "dias") + " de atraso.";
        }
        if (tela.confirmar("Registrar devolução", mensagem, "Registrar devolução", false)
                && executar(() -> biblioteca.devolver(emprestimo))) {
            tela.informar("Devolução de “" + emprestimo.getLivro().getTitulo() + "” registrada"
                    + (atraso > 0 ? " (com " + Formato.plural(atraso, "dia", "dias") + " de atraso)." : "."));
            atualizarTudo.run();
        }
    }

    /** Vai para a tela de empréstimos mostrando o histórico de um usuário. */
    public void mostrarDoUsuario(Usuario usuario) {
        tela.mostrar(TelaPrincipal.EMPRESTIMOS);
        painel.filtrarPorUsuario(usuario.getMatricula());
    }

    public void mostrarSituacao(String situacao) {
        tela.mostrar(TelaPrincipal.EMPRESTIMOS);
        painel.mostrarSituacao(situacao);
    }
}
