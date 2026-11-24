package com.mycompany.bibliotecasistema.visao;

import com.mycompany.bibliotecasistema.modelo.Emprestimo;
import com.mycompany.bibliotecasistema.modelo.Livro;
import com.mycompany.bibliotecasistema.modelo.Usuario;
import com.mycompany.bibliotecasistema.visao.componentes.DialogoFormulario;
import com.mycompany.bibliotecasistema.visao.componentes.Formato;
import com.mycompany.bibliotecasistema.visao.componentes.Tema;
import java.awt.Component;
import java.awt.Window;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Formulário de empréstimo: escolhe-se o usuário e o livro em listas (sem
 * precisar decorar matrícula). O prazo aparece na hora e, se o usuário não
 * puder pegar livro, o motivo aparece antes de tentar salvar.
 */
public class DialogoEmprestimo extends DialogoFormulario {

    private final JComboBox<Usuario> usuario;
    private final JComboBox<Livro> livro;
    private final JLabel devolucao = new JLabel();
    private final Campo campoUsuario;
    private final Campo campoLivro;
    private final Function<Usuario, String> verificar;
    private final Function<Usuario, LocalDate> prazo;
    private final BiFunction<Livro, Usuario, Emprestimo> emprestar;
    private Emprestimo resultado;

    public DialogoEmprestimo(Window dono, List<Usuario> usuarios, List<Livro> livros,
            Livro livroInicial, Usuario usuarioInicial,
            Function<Usuario, String> verificar, Function<Usuario, LocalDate> prazo,
            BiFunction<Livro, Usuario, Emprestimo> emprestar) {
        super(dono, "Novo empréstimo", "Escolha quem vai levar o livro. O prazo é calculado sozinho.", "Emprestar");
        this.verificar = verificar;
        this.prazo = prazo;
        this.emprestar = emprestar;

        usuario = new JComboBox<>(usuarios.toArray(Usuario[]::new));
        usuario.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                    boolean selecionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
                if (valor instanceof Usuario u) {
                    setText(u.getNome() + "  ·  " + u.getMatricula() + "  ·  " + u.getTipo());
                } else {
                    setText("Escolha o usuário");
                }
                return this;
            }
        });
        livro = new JComboBox<>(livros.toArray(Livro[]::new));
        livro.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                    boolean selecionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
                setText(valor instanceof Livro l ? l.getTitulo() + "  ·  " + l.getAutor() : "Escolha o livro");
                return this;
            }
        });
        selecionar(usuario, usuarioInicial == null ? -1 : usuarioInicial.getId(), Usuario::getId);
        selecionar(livro, livroInicial == null ? -1 : livroInicial.getId(), Livro::getId);

        campoUsuario = adicionarCampo("Usuário", usuario, "Dica: digite a primeira letra do nome para achar mais rápido.");
        campoLivro = adicionarCampo("Livro", livro, "Só aparecem os livros disponíveis.");
        Tema.fonte(devolucao, "large");
        adicionarCampo("Devolver até", devolucao, "");

        usuario.addActionListener(e -> atualizarUsuario());
        atualizarUsuario();
        finalizarLayout();
    }

    private static <T> void selecionar(JComboBox<T> combo, int id, Function<T, Integer> idDe) {
        combo.setSelectedIndex(-1);
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (idDe.apply(combo.getItemAt(i)) == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    /** Mostra o prazo do usuário escolhido e, se ele estiver bloqueado, o motivo. */
    private void atualizarUsuario() {
        Usuario u = (Usuario) usuario.getSelectedItem();
        campoUsuario.limpar();
        botaoConfirmar.setEnabled(true);
        if (u == null) {
            devolucao.setText("—");
            return;
        }
        devolucao.setText(Formato.data(prazo.apply(u)) + "   (" + u.getPrazoDias() + " dias, prazo de "
                + u.getTipo().toLowerCase() + ")");
        try {
            String motivo = verificar.apply(u);
            if (motivo != null) {
                campoUsuario.erro(motivo);
                botaoConfirmar.setEnabled(false);
            }
        } catch (RuntimeException e) {
            campoUsuario.erro(e.getMessage());
        }
        pack();
    }

    @Override
    protected boolean validar() {
        boolean ok = true;
        Usuario u = (Usuario) usuario.getSelectedItem();
        if (u == null) {
            campoUsuario.erro("Escolha o usuário.");
            ok = false;
        } else {
            String motivo = verificar.apply(u);
            if (motivo != null) {
                campoUsuario.erro(motivo);
                ok = false;
            }
        }
        if (livro.getSelectedItem() == null) {
            campoLivro.erro("Escolha o livro.");
            ok = false;
        }
        return ok;
    }

    @Override
    protected void confirmar() {
        resultado = emprestar.apply((Livro) livro.getSelectedItem(), (Usuario) usuario.getSelectedItem());
    }

    public Emprestimo getResultado() {
        return resultado;
    }
}
