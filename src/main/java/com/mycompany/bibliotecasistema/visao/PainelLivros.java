package com.mycompany.bibliotecasistema.visao;

import com.mycompany.bibliotecasistema.modelo.Livro;
import com.mycompany.bibliotecasistema.visao.componentes.Formato;
import com.mycompany.bibliotecasistema.visao.componentes.PainelLista;
import com.mycompany.bibliotecasistema.visao.componentes.TabelaPadrao.Coluna;
import com.mycompany.bibliotecasistema.visao.componentes.Tema.Tom;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JComboBox;

public class PainelLivros extends PainelLista<Livro> {

    private static final String TODOS = "Todos os livros";
    private static final String DISPONIVEIS = "Só disponíveis";
    private static final String EMPRESTADOS = "Só emprestados";

    private final JComboBox<String> filtro = new JComboBox<>(new String[]{TODOS, DISPONIVEIS, EMPRESTADOS});
    private final Acao editar;
    private final Acao excluir;
    private final Acao emprestar;

    public PainelLivros() {
        super("Livros", "Buscar por título ou autor", List.of(
                Coluna.<Livro>numero("ID", 50, Livro::getId),
                Coluna.<Livro>texto("Título", 340, Livro::getTitulo),
                Coluna.<Livro>texto("Autor", 220, Livro::getAutor),
                Coluna.<Livro>numero("Ano", 70, Livro::getAno),
                Coluna.<Livro>situacao("Disponível", 110, l -> l.isDisponivel() ? "Sim" : "Não",
                        texto -> "Sim".equals(texto) ? Tom.SUCESSO : Tom.ALERTA)),
                Livro::getId,
                l -> l.getTitulo() + " " + l.getAutor());

        definirAcaoPrincipal("+ Novo livro", "Cadastrar um livro (Ctrl+N)");
        tabela.setMensagemVazia("Nenhum livro cadastrado. Clique em “+ Novo livro” para começar.");

        filtro.setToolTipText("Mostrar todos ou só uma situação");
        filtro.addActionListener(e -> setFiltroExtra(switch ((String) filtro.getSelectedItem()) {
            case DISPONIVEIS -> Livro::isDisponivel;
            case EMPRESTADOS -> l -> !l.isDisponivel();
            default -> l -> true;
        }));
        adicionarFiltro(filtro);

        emprestar = criarAcao("Emprestar", "Registrar o empréstimo do livro selecionado", Livro::isDisponivel);
        editar = criarAcao("Editar", "Editar o livro selecionado (Enter ou duplo clique)", l -> true);
        excluir = criarAcao("Excluir", "Excluir o livro selecionado (Delete). Livros emprestados não podem ser excluídos.",
                Livro::isDisponivel);
    }

    @Override
    protected String resumir(List<Livro> livros) {
        long disponiveis = livros.stream().filter(Livro::isDisponivel).count();
        return Formato.plural(livros.size(), "livro no acervo", "livros no acervo")
                + "  ·  " + Formato.plural(disponiveis, "disponível", "disponíveis");
    }

    public void aoEditar(Consumer<Livro> acao) {
        editar.aoExecutar(acao);
        tabela.aoAbrir(acao);
    }

    public void aoExcluir(Consumer<Livro> acao) {
        excluir.aoExecutar(acao);
        tabela.aoExcluir(acao);
    }

    public void aoEmprestar(Consumer<Livro> acao) {
        emprestar.aoExecutar(acao);
    }

    /** Usado pelos cartões da tela inicial. */
    public void mostrarSomenteDisponiveis() {
        busca.setText("");
        filtro.setSelectedItem(DISPONIVEIS);
    }

    public void mostrarTodos() {
        busca.setText("");
        filtro.setSelectedItem(TODOS);
    }
}
