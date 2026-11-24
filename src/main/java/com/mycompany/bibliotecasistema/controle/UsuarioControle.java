package com.mycompany.bibliotecasistema.controle;

import com.mycompany.bibliotecasistema.modelo.Biblioteca;
import com.mycompany.bibliotecasistema.modelo.Emprestimo;
import com.mycompany.bibliotecasistema.modelo.Usuario;
import com.mycompany.bibliotecasistema.visao.DialogoUsuario;
import com.mycompany.bibliotecasistema.visao.PainelUsuarios;
import com.mycompany.bibliotecasistema.visao.TelaPrincipal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UsuarioControle extends ControleBase {

    private final PainelUsuarios painel;

    public UsuarioControle(Biblioteca biblioteca, TelaPrincipal tela, EmprestimoControle emprestimos,
            Runnable atualizarTudo) {
        super(biblioteca, tela, atualizarTudo);
        this.painel = tela.getPainelUsuarios();
        painel.aoNovo(() -> abrirFormulario(null));
        painel.aoEditar(this::abrirFormulario);
        painel.aoExcluir(this::excluir);
        painel.aoVerEmprestimos(emprestimos::mostrarDoUsuario);
        painel.aoEmprestar(usuario -> emprestimos.novoEmprestimo(null, usuario));
    }

    /** Junta cada usuário com a situação dos empréstimos dele. */
    public void mostrar(List<Usuario> usuarios, List<Emprestimo> emprestimos) {
        LocalDate hoje = biblioteca.hoje();
        Map<Integer, List<Emprestimo>> abertosPorUsuario = emprestimos.stream()
                .filter(Emprestimo::isAberto)
                .collect(Collectors.groupingBy(e -> e.getUsuario().getId()));
        painel.mostrar(usuarios.stream().map(u -> {
            List<Emprestimo> abertos = abertosPorUsuario.getOrDefault(u.getId(), List.of());
            return new PainelUsuarios.Linha(u, abertos.size(), abertos.stream().anyMatch(e -> e.isAtrasado(hoje)));
        }).toList());
    }

    private void abrirFormulario(Usuario existente) {
        DialogoUsuario dialogo = new DialogoUsuario(tela, existente, biblioteca::salvarUsuario);
        if (dialogo.exibir()) {
            Usuario u = dialogo.getResultado();
            tela.informar((existente == null ? u.getTipo() + " " + u.getNome() + " cadastrado." : u.getNome() + " salvo."));
            atualizarTudo.run();
        }
    }

    private void excluir(Usuario usuario) {
        boolean confirmado = tela.confirmar("Excluir usuário",
                "Excluir " + usuario.getNome() + " (" + usuario.getMatricula() + ")?\nEssa ação não pode ser desfeita.",
                "Excluir", true);
        if (confirmado && executar(() -> biblioteca.excluirUsuario(usuario))) {
            tela.informar(usuario.getNome() + " excluído.");
            atualizarTudo.run();
        }
    }
}
