package com.mycompany.bibliotecasistema.visao;

import com.mycompany.bibliotecasistema.modelo.Emprestimo;
import com.mycompany.bibliotecasistema.visao.componentes.Formato;
import com.mycompany.bibliotecasistema.visao.componentes.PainelLista;
import com.mycompany.bibliotecasistema.visao.componentes.TabelaPadrao.Coluna;
import com.mycompany.bibliotecasistema.visao.componentes.Tema.Tom;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JComboBox;

public class PainelEmprestimos extends PainelLista<Emprestimo> {

    public static final String EM_ABERTO = "Em aberto";
    public static final String ATRASADOS = "Atrasados";
    public static final String DEVOLVIDOS = "Devolvidos";
    public static final String TODOS = "Todos";

    private final JComboBox<String> filtro = new JComboBox<>(new String[]{EM_ABERTO, ATRASADOS, DEVOLVIDOS, TODOS});
    private final Acao devolver;

    public PainelEmprestimos() {
        super("Empréstimos", "Buscar por livro, autor, usuário ou matrícula", List.of(
                Coluna.<Emprestimo>numero("ID", 50, Emprestimo::getId),
                Coluna.<Emprestimo>texto("Livro", 230, e -> e.getLivro().getTitulo()),
                Coluna.<Emprestimo>texto("Usuário", 160, e -> e.getUsuario().getNome()),
                Coluna.<Emprestimo>data("Empréstimo", 100, Emprestimo::getDataEmprestimo),
                Coluna.<Emprestimo>data("Prazo", 100, Emprestimo::getDataDevolucaoPrevista),
                Coluna.<Emprestimo>data("Devolução", 100, Emprestimo::getDataDevolucao),
                Coluna.<Emprestimo>situacao("Situação", 170, e -> e.getSituacao(LocalDate.now()),
                        PainelEmprestimos::tomDaSituacao)),
                Emprestimo::getId,
                e -> e.getLivro().getTitulo() + " " + e.getLivro().getAutor() + " "
                + e.getUsuario().getNome() + " " + e.getUsuario().getMatricula());

        definirAcaoPrincipal("+ Novo empréstimo", "Registrar um empréstimo (Ctrl+N)");
        tabela.setMensagemVazia("Nenhum empréstimo nesta lista.");

        filtro.setToolTipText("Escolha quais empréstimos mostrar");
        filtro.addActionListener(e -> aplicarFiltro());
        adicionarFiltro(filtro);
        aplicarFiltro();

        devolver = criarAcao("Registrar devolução",
                "Registrar que o livro foi devolvido hoje (Enter ou duplo clique)", Emprestimo::isAberto);
    }

    /** Cor da situação, usada também na tela inicial. */
    public static Tom tomDaSituacao(String situacao) {
        if (situacao.startsWith("Atrasado")) {
            return Tom.PERIGO;
        }
        if (situacao.equals("Vence hoje") || situacao.equals("Vence amanhã")) {
            return Tom.ALERTA;
        }
        if (situacao.startsWith("Vence")) {
            return Tom.SUCESSO;
        }
        return Tom.NEUTRO;
    }

    private void aplicarFiltro() {
        LocalDate hoje = LocalDate.now();
        setFiltroExtra(switch ((String) filtro.getSelectedItem()) {
            case EM_ABERTO -> Emprestimo::isAberto;
            case ATRASADOS -> e -> e.isAtrasado(hoje);
            case DEVOLVIDOS -> e -> !e.isAberto();
            default -> e -> true;
        });
    }

    @Override
    protected String resumir(List<Emprestimo> emprestimos) {
        LocalDate hoje = LocalDate.now();
        long abertos = emprestimos.stream().filter(Emprestimo::isAberto).count();
        long atrasados = emprestimos.stream().filter(e -> e.isAtrasado(hoje)).count();
        return Formato.plural(abertos, "em aberto", "em aberto")
                + "  ·  " + Formato.plural(atrasados, "atrasado", "atrasados")
                + "  ·  " + Formato.plural(emprestimos.size(), "no histórico", "no histórico");
    }

    public void aoDevolver(Consumer<Emprestimo> acao) {
        devolver.aoExecutar(acao);
        tabela.aoAbrir(e -> {
            if (e.isAberto()) {
                acao.accept(e);
            }
        });
    }

    /** Mostra só os empréstimos de um usuário (todo o histórico dele). */
    public void filtrarPorUsuario(String matricula) {
        filtro.setSelectedItem(TODOS);
        busca.setText(matricula);
    }

    /** Usado pelos cartões da tela inicial. */
    public void mostrarSituacao(String situacao) {
        busca.setText("");
        filtro.setSelectedItem(situacao);
    }
}
