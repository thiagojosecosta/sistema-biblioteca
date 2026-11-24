package com.mycompany.bibliotecasistema.controle;

import com.mycompany.bibliotecasistema.modelo.Biblioteca;
import com.mycompany.bibliotecasistema.modelo.Emprestimo;
import com.mycompany.bibliotecasistema.modelo.Livro;
import com.mycompany.bibliotecasistema.modelo.Usuario;
import com.mycompany.bibliotecasistema.visao.PainelEmprestimos;
import com.mycompany.bibliotecasistema.visao.PainelInicio;
import com.mycompany.bibliotecasistema.visao.TelaPrincipal;
import java.util.List;

/**
 * Monta os controles de cada área e mantém todas as telas em dia: depois de
 * qualquer alteração, tudo é recarregado do banco (um empréstimo muda a tela
 * de livros, de usuários e o início ao mesmo tempo).
 */
public class PrincipalControle {

    private final Biblioteca biblioteca;
    private final TelaPrincipal tela;
    private final EmprestimoControle emprestimos;
    private final LivroControle livros;
    private final UsuarioControle usuarios;

    public PrincipalControle(Biblioteca biblioteca, TelaPrincipal tela) {
        this.biblioteca = biblioteca;
        this.tela = tela;
        this.emprestimos = new EmprestimoControle(biblioteca, tela, this::atualizarTudo);
        this.livros = new LivroControle(biblioteca, tela, emprestimos, this::atualizarTudo);
        this.usuarios = new UsuarioControle(biblioteca, tela, emprestimos, this::atualizarTudo);
        configurarInicio(tela.getPainelInicio());
        tela.aoAtualizar(() -> {
            if (atualizarTudo()) {
                tela.informar("Dados atualizados.");
            }
        });
    }

    private void configurarInicio(PainelInicio inicio) {
        inicio.aoNovoEmprestimo(() -> emprestimos.novoEmprestimo(null, null));
        inicio.aoDevolver(emprestimos::devolver);
        inicio.aoClicarLivros(() -> {
            tela.mostrar(TelaPrincipal.LIVROS);
            tela.getPainelLivros().mostrarTodos();
        });
        inicio.aoClicarDisponiveis(() -> {
            tela.mostrar(TelaPrincipal.LIVROS);
            tela.getPainelLivros().mostrarSomenteDisponiveis();
        });
        inicio.aoClicarAbertos(() -> emprestimos.mostrarSituacao(PainelEmprestimos.EM_ABERTO));
        inicio.aoClicarAtrasados(() -> emprestimos.mostrarSituacao(PainelEmprestimos.ATRASADOS));
    }

    public void iniciar() {
        tela.setVisible(true);
        atualizarTudo();
    }

    /** Recarrega tudo do banco. Devolve false se não conseguiu (e avisa o usuário). */
    public boolean atualizarTudo() {
        try {
            List<Livro> listaLivros = biblioteca.listarLivros();
            List<Usuario> listaUsuarios = biblioteca.listarUsuarios();
            List<Emprestimo> listaEmprestimos = biblioteca.listarEmprestimos();

            livros.mostrar(listaLivros);
            usuarios.mostrar(listaUsuarios, listaEmprestimos);
            emprestimos.mostrar(listaEmprestimos);
            tela.getPainelInicio().mostrar(listaLivros.size(),
                    listaLivros.stream().filter(Livro::isDisponivel).count(),
                    listaEmprestimos.stream().filter(Emprestimo::isAberto).toList());
            tela.marcarAtualizado();
            return true;
        } catch (RuntimeException e) {
            tela.mostrarErro(e.getMessage());
            return false;
        }
    }
}
