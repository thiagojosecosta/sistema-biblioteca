package com.mycompany.bibliotecasistema.visao.componentes;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

/**
 * Tabela usada em todas as telas: somente leitura, ordenável pelo cabeçalho,
 * com busca sem diferenciar maiúsculas e acentos, mensagem quando está vazia,
 * Enter/duplo clique para abrir e Delete para excluir.
 */
public class TabelaPadrao<T> extends JTable {

    /** Uma coluna: nome, tipo do valor, como tirar o valor da linha e largura preferida. */
    public record Coluna<T>(String nome, Class<?> tipo, Function<T, ?> valor, int largura, TableCellRenderer renderizador) {

        public static <T> Coluna<T> texto(String nome, int largura, Function<T, String> valor) {
            return new Coluna<>(nome, String.class, valor, largura, null);
        }

        public static <T> Coluna<T> numero(String nome, int largura, Function<T, Integer> valor) {
            return new Coluna<>(nome, Integer.class, valor, largura, centralizado());
        }

        public static <T> Coluna<T> data(String nome, int largura, Function<T, LocalDate> valor) {
            return new Coluna<>(nome, LocalDate.class, valor, largura, new RenderizadorData());
        }

        /** Texto com cor conforme o significado (ex.: "Atrasado" em vermelho). */
        public static <T> Coluna<T> situacao(String nome, int largura, Function<T, String> valor,
                Function<String, Tema.Tom> tom) {
            return new Coluna<>(nome, String.class, valor, largura, new RenderizadorSituacao(tom));
        }
    }

    private final List<Coluna<T>> colunas;
    private final Function<T, Object> chave;
    private final Modelo modelo = new Modelo();
    private final TableRowSorter<Modelo> ordenador;
    private Function<T, String> textoBusca = item -> "";
    private Predicate<T> filtroExtra = item -> true;
    private String termo = "";
    private String mensagemVazia = "Nada para mostrar.";
    private Consumer<T> aoAbrir;
    private Consumer<T> aoExcluir;

    /**
     * @param chave identifica a linha (ex.: o id) para manter a seleção depois de atualizar
     */
    public TabelaPadrao(List<Coluna<T>> colunas, Function<T, Object> chave) {
        this.colunas = List.copyOf(colunas);
        this.chave = chave;
        setModel(modelo);
        ordenador = new TableRowSorter<>(modelo);
        setRowSorter(ordenador);
        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTableHeader().setReorderingAllowed(false);

        for (int i = 0; i < this.colunas.size(); i++) {
            Coluna<T> coluna = this.colunas.get(i);
            TableColumn tc = getColumnModel().getColumn(i);
            tc.setPreferredWidth(coluna.largura());
            if (coluna.renderizador() != null) {
                tc.setCellRenderer(coluna.renderizador());
            }
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && rowAtPoint(e.getPoint()) >= 0) {
                    executar(aoAbrir);
                }
            }
        });
        atalho(KeyEvent.VK_ENTER, "abrirItem", () -> executar(aoAbrir));
        atalho(KeyEvent.VK_DELETE, "excluirItem", () -> executar(aoExcluir));
    }

    private void atalho(int tecla, String nome, Runnable acao) {
        KeyStroke ks = KeyStroke.getKeyStroke(tecla, 0);
        getInputMap(WHEN_FOCUSED).put(ks, nome);
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ks, nome);
        getActionMap().put(nome, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acao.run();
            }
        });
    }

    private void executar(Consumer<T> acao) {
        T selecionado = getSelecionado();
        if (acao != null && selecionado != null) {
            acao.accept(selecionado);
        }
    }

    public void aoAbrir(Consumer<T> acao) {
        this.aoAbrir = acao;
    }

    public void aoExcluir(Consumer<T> acao) {
        this.aoExcluir = acao;
    }

    public void aoMudarSelecao(Runnable acao) {
        getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                acao.run();
            }
        });
    }

    public void setMensagemVazia(String mensagem) {
        this.mensagemVazia = mensagem;
    }

    public void setTextoBusca(Function<T, String> textoBusca) {
        this.textoBusca = textoBusca;
    }

    public void filtrar(String texto) {
        this.termo = normalizar(texto);
        aplicarFiltro();
    }

    public void setFiltroExtra(Predicate<T> filtro) {
        this.filtroExtra = filtro;
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        ordenador.setRowFilter(new RowFilter<Modelo, Integer>() {
            @Override
            public boolean include(Entry<? extends Modelo, ? extends Integer> linha) {
                T item = modelo.dados.get(linha.getIdentifier());
                return filtroExtra.test(item)
                        && (termo.isEmpty() || normalizar(textoBusca.apply(item)).contains(termo));
            }
        });
    }

    /** Troca os dados mantendo a mesma linha selecionada, se ela ainda existir. */
    public void setDados(List<T> dados) {
        T anterior = getSelecionado();
        Object chaveAnterior = anterior == null ? null : chave.apply(anterior);
        modelo.dados = new ArrayList<>(dados);
        modelo.fireTableDataChanged();
        if (chaveAnterior == null) {
            return;
        }
        for (int i = 0; i < modelo.dados.size(); i++) {
            if (chaveAnterior.equals(chave.apply(modelo.dados.get(i)))) {
                int linhaVisivel = convertRowIndexToView(i);
                if (linhaVisivel >= 0) {
                    setRowSelectionInterval(linhaVisivel, linhaVisivel);
                }
                return;
            }
        }
    }

    public T getSelecionado() {
        int linha = getSelectedRow();
        return linha < 0 ? null : modelo.dados.get(convertRowIndexToModel(linha));
    }

    /** Desenha uma mensagem amigável quando não há linhas. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getRowCount() > 0) {
            return;
        }
        String texto = modelo.dados.isEmpty() ? mensagemVazia : "Nenhum resultado para essa busca.";
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(UIManager.getColor("Label.disabledForeground"));
        g2.setFont(getFont().deriveFont(getFont().getSize2D() + 1f));
        FontMetrics fm = g2.getFontMetrics();
        int x = Math.max(12, (getWidth() - fm.stringWidth(texto)) / 2);
        int y = Math.max(fm.getAscent() + 32, getVisibleRect().height / 3);
        g2.drawString(texto, x, y);
        g2.dispose();
    }

    static String normalizar(String texto) {
        return Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private static DefaultTableCellRenderer centralizado() {
        DefaultTableCellRenderer renderizador = new DefaultTableCellRenderer();
        renderizador.setHorizontalAlignment(SwingConstants.CENTER);
        return renderizador;
    }

    private final class Modelo extends AbstractTableModel {

        private List<T> dados = List.of();

        @Override
        public int getRowCount() {
            return dados.size();
        }

        @Override
        public int getColumnCount() {
            return colunas.size();
        }

        @Override
        public String getColumnName(int coluna) {
            return colunas.get(coluna).nome();
        }

        @Override
        public Class<?> getColumnClass(int coluna) {
            return colunas.get(coluna).tipo();
        }

        @Override
        public Object getValueAt(int linha, int coluna) {
            return colunas.get(coluna).valor().apply(dados.get(linha));
        }
    }

    private static final class RenderizadorData extends DefaultTableCellRenderer {

        RenderizadorData() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        protected void setValue(Object valor) {
            setText(valor instanceof LocalDate data ? Formato.data(data) : "—");
        }
    }

    private static final class RenderizadorSituacao extends DefaultTableCellRenderer {

        private final Function<String, Tema.Tom> tom;

        RenderizadorSituacao(Function<String, Tema.Tom> tom) {
            this.tom = tom;
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado,
                boolean foco, int linha, int coluna) {
            super.getTableCellRendererComponent(tabela, valor, selecionado, foco, linha, coluna);
            String texto = valor == null ? "" : valor.toString();
            setText(texto.isEmpty() ? "" : "●  " + texto);
            setFont(getFont().deriveFont(Font.BOLD));
            if (!selecionado && !texto.isEmpty()) {
                setForeground(Tema.cor(tom.apply(texto)));
            }
            return this;
        }
    }
}
