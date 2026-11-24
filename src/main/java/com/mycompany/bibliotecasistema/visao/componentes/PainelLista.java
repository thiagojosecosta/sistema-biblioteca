package com.mycompany.bibliotecasistema.visao.componentes;

import com.mycompany.bibliotecasistema.visao.componentes.TabelaPadrao.Coluna;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Molde comum das telas de lista (Livros, Usuários, Empréstimos), para que
 * todas funcionem do mesmo jeito: título e resumo no alto, ação principal à
 * direita, busca e filtros, tabela no meio e ações da linha selecionada embaixo.
 */
public abstract class PainelLista<T> extends JPanel implements Painel {

    protected final TabelaPadrao<T> tabela;
    protected final CampoBusca busca;
    private final JLabel resumo = new JLabel(" ");
    private final JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    private final JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    private final JPanel areaPrincipal = new JPanel(new GridBagLayout());
    private final List<Acao> listaAcoes = new ArrayList<>();
    private JButton botaoPrincipal;
    private Runnable acaoPrincipal = () -> {
    };
    private List<T> dados = List.of();

    protected PainelLista(String titulo, String dicaBusca, List<Coluna<T>> colunas,
            Function<T, Object> chave, Function<T, String> textoBusca) {
        super(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 16, 28));

        tabela = new TabelaPadrao<>(colunas, chave);
        tabela.setTextoBusca(textoBusca);
        tabela.aoMudarSelecao(this::atualizarEstadoAcoes);

        busca = new CampoBusca(dicaBusca);
        busca.aoDigitar(texto -> {
            tabela.filtrar(texto);
            atualizarResumo();
            atualizarEstadoAcoes();
        });

        JLabel rotuloTitulo = new JLabel(titulo);
        Tema.fonte(rotuloTitulo, "h1");
        Tema.suave(resumo);
        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(rotuloTitulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(resumo);

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.add(textos, BorderLayout.WEST);
        cabecalho.add(areaPrincipal, BorderLayout.EAST);

        filtros.add(busca);

        JPanel topo = new JPanel(new BorderLayout(0, 16));
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(filtros, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(acoes, BorderLayout.SOUTH);
    }

    /** O botão de destaque no canto superior direito (ex.: "+ Novo livro"). */
    protected void definirAcaoPrincipal(String texto, String dica) {
        botaoPrincipal = new JButton(texto);
        botaoPrincipal.setToolTipText(dica);
        Tema.destacar(botaoPrincipal);
        botaoPrincipal.addActionListener(e -> acaoPrincipal.run());
        areaPrincipal.add(botaoPrincipal);
    }

    public void aoNovo(Runnable acao) {
        this.acaoPrincipal = acao;
    }

    /** Um botão que age sobre a linha selecionada; fica desligado quando não faz sentido. */
    protected Acao criarAcao(String texto, String dica, Predicate<T> habilitar) {
        JButton botao = new JButton(texto);
        botao.setToolTipText(dica);
        botao.setEnabled(false);
        Acao acao = new Acao(botao, habilitar);
        botao.addActionListener(e -> {
            T selecionado = tabela.getSelecionado();
            if (selecionado != null && habilitar.test(selecionado)) {
                acao.executar.accept(selecionado);
            }
        });
        if (!listaAcoes.isEmpty()) {
            acoes.add(Box.createHorizontalStrut(8));
        }
        acoes.add(botao);
        listaAcoes.add(acao);
        return acao;
    }

    /** Um filtro extra ao lado da busca (ex.: uma lista "Em aberto / Todos"). */
    protected void adicionarFiltro(JComponent componente) {
        filtros.add(Box.createHorizontalStrut(12));
        filtros.add(componente);
    }

    protected void setFiltroExtra(Predicate<T> filtro) {
        tabela.setFiltroExtra(filtro);
        atualizarResumo();
        atualizarEstadoAcoes();
    }

    public void mostrar(List<T> dados) {
        this.dados = List.copyOf(dados);
        tabela.setDados(this.dados);
        atualizarResumo();
        atualizarEstadoAcoes();
    }

    /** Frase curta sobre os dados, mostrada abaixo do título (ex.: "3 livros · 2 disponíveis"). */
    protected abstract String resumir(List<T> dados);

    private void atualizarResumo() {
        String texto = resumir(dados);
        int visiveis = tabela.getRowCount();
        if (visiveis != dados.size()) {
            texto += "  ·  mostrando " + visiveis;
        }
        resumo.setText(texto);
    }

    private void atualizarEstadoAcoes() {
        T selecionado = tabela.getSelecionado();
        for (Acao acao : listaAcoes) {
            acao.botao.setEnabled(selecionado != null && acao.habilitar.test(selecionado));
        }
    }

    @Override
    public void acionarPrincipal() {
        if (botaoPrincipal != null) {
            botaoPrincipal.doClick();
        }
    }

    @Override
    public void focarBusca() {
        busca.requestFocusInWindow();
        busca.selectAll();
    }

    protected final class Acao {

        private final JButton botao;
        private final Predicate<T> habilitar;
        private Consumer<T> executar = item -> {
        };

        private Acao(JButton botao, Predicate<T> habilitar) {
            this.botao = botao;
            this.habilitar = habilitar;
        }

        public void aoExecutar(Consumer<T> acao) {
            this.executar = acao;
        }
    }
}
