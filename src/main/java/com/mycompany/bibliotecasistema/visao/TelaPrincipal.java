package com.mycompany.bibliotecasistema.visao;

import com.formdev.flatlaf.FlatClientProperties;
import com.mycompany.bibliotecasistema.visao.componentes.BarraStatus;
import com.mycompany.bibliotecasistema.visao.componentes.BordaLinha;
import com.mycompany.bibliotecasistema.visao.componentes.Formato;
import com.mycompany.bibliotecasistema.visao.componentes.Painel;
import com.mycompany.bibliotecasistema.visao.componentes.Tema;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/** Janela principal: menu lateral, a tela escolhida no centro e a barra de status embaixo. */
public class TelaPrincipal extends JFrame {

    public static final String INICIO = "inicio";
    public static final String LIVROS = "livros";
    public static final String USUARIOS = "usuarios";
    public static final String EMPRESTIMOS = "emprestimos";

    private final CardLayout cartoes = new CardLayout();
    private final JPanel conteudo = new JPanel(cartoes);
    private final JPanel itensMenu = new JPanel();
    private final ButtonGroup grupoMenu = new ButtonGroup();
    private final Map<String, JToggleButton> botoesMenu = new LinkedHashMap<>();
    private final Map<String, Painel> paineis = new LinkedHashMap<>();

    private final PainelInicio painelInicio = new PainelInicio();
    private final PainelLivros painelLivros = new PainelLivros();
    private final PainelUsuarios painelUsuarios = new PainelUsuarios();
    private final PainelEmprestimos painelEmprestimos = new PainelEmprestimos();
    private final BarraStatus barraStatus = new BarraStatus();

    private String telaAtual = INICIO;
    private Runnable aoAtualizar = () -> {
    };

    public TelaPrincipal() {
        super("Biblioteca");

        itensMenu.setLayout(new BoxLayout(itensMenu, BoxLayout.Y_AXIS));
        adicionarTela(INICIO, "Início", painelInicio, 1);
        adicionarTela(LIVROS, "Livros", painelLivros, 2);
        adicionarTela(USUARIOS, "Usuários", painelUsuarios, 3);
        adicionarTela(EMPRESTIMOS, "Empréstimos", painelEmprestimos, 4);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(criarMenuLateral(), BorderLayout.WEST);
        raiz.add(conteudo, BorderLayout.CENTER);
        raiz.add(barraStatus, BorderLayout.SOUTH);
        setContentPane(raiz);

        atalho(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "atualizar", () -> aoAtualizar.run());
        atalho(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "novo",
                () -> paineis.get(telaAtual).acionarPrincipal());
        atalho(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "buscar",
                () -> paineis.get(telaAtual).focarBusca());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 640));
        setSize(1200, 760);
        setLocationRelativeTo(null);
        mostrar(INICIO);
    }

    private JPanel criarMenuLateral() {
        JLabel nome = new JLabel("Biblioteca");
        Tema.fonte(nome, "h2");
        JLabel subtitulo = new JLabel("Acervo e empréstimos");
        Tema.suave(subtitulo);
        JPanel marca = new JPanel();
        marca.setOpaque(false);
        marca.setLayout(new BoxLayout(marca, BoxLayout.Y_AXIS));
        marca.add(nome);
        marca.add(subtitulo);
        marca.add(Box.createVerticalStrut(24));
        itensMenu.setOpaque(false);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(marca, BorderLayout.NORTH);
        topo.add(itensMenu, BorderLayout.CENTER);

        JButton atualizar = new JButton("Atualizar dados");
        atualizar.setToolTipText("Buscar os dados de novo no banco (F5)");
        atualizar.addActionListener(e -> aoAtualizar.run());
        JToggleButton temaEscuro = new JToggleButton("Tema escuro", Tema.isEscuro());
        temaEscuro.setToolTipText("Alternar entre tema claro e escuro");
        temaEscuro.addActionListener(e -> {
            Tema.alternar();
            aoAtualizar.run();
        });
        JPanel rodape = new JPanel();
        rodape.setOpaque(false);
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        for (AbstractButton botao : new AbstractButton[]{atualizar, temaEscuro}) {
            estiloMenu(botao);
            rodape.add(botao);
            rodape.add(Box.createVerticalStrut(4));
        }

        JPanel lateral = new JPanel(new BorderLayout());
        lateral.putClientProperty(FlatClientProperties.STYLE, "background: darken($Panel.background,3%)");
        lateral.setBorder(new BordaLinha(BordaLinha.Lado.DIREITA, 24, 16, 16, 16));
        lateral.setPreferredSize(new Dimension(220, 0));
        lateral.add(topo, BorderLayout.NORTH);
        lateral.add(rodape, BorderLayout.SOUTH);
        return lateral;
    }

    private void adicionarTela(String chave, String rotulo, JComponent painel, int numero) {
        conteudo.add(painel, chave);
        paineis.put(chave, (Painel) painel);

        JToggleButton botao = new JToggleButton(rotulo);
        botao.setToolTipText(rotulo + " (Ctrl+" + numero + ")");
        botao.addActionListener(e -> mostrar(chave));
        estiloMenu(botao);
        botao.putClientProperty(FlatClientProperties.STYLE, "margin: 8,12,8,12; font: +1");
        grupoMenu.add(botao);
        botoesMenu.put(chave, botao);
        itensMenu.add(botao);
        itensMenu.add(Box.createVerticalStrut(4));

        atalho(KeyStroke.getKeyStroke(KeyEvent.VK_0 + numero, InputEvent.CTRL_DOWN_MASK), "tela" + numero,
                () -> mostrar(chave));
    }

    private static void estiloMenu(AbstractButton botao) {
        botao.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
        botao.putClientProperty(FlatClientProperties.STYLE, "margin: 6,12,6,12");
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        botao.setFocusable(false);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, botao.getPreferredSize().height + 8));
    }

    private void atalho(KeyStroke tecla, String nome, Runnable acao) {
        JComponent raiz = getRootPane();
        raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(tecla, nome);
        raiz.getActionMap().put(nome, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acao.run();
            }
        });
    }

    public void mostrar(String chave) {
        telaAtual = chave;
        cartoes.show(conteudo, chave);
        botoesMenu.get(chave).setSelected(true);
        SwingUtilities.invokeLater(() -> paineis.get(chave).focarBusca());
    }

    public void aoAtualizar(Runnable acao) {
        this.aoAtualizar = acao;
    }

    public PainelInicio getPainelInicio() {
        return painelInicio;
    }

    public PainelLivros getPainelLivros() {
        return painelLivros;
    }

    public PainelUsuarios getPainelUsuarios() {
        return painelUsuarios;
    }

    public PainelEmprestimos getPainelEmprestimos() {
        return painelEmprestimos;
    }

    /** Confirma na barra de status o que acabou de acontecer. */
    public void informar(String mensagem) {
        barraStatus.mostrar(mensagem);
    }

    public void marcarAtualizado() {
        barraStatus.marcarAtualizado();
    }

    /** Uma regra impediu a ação: explica o motivo. */
    public void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(this, Formato.html(mensagem, 380), "Não foi possível concluir",
                JOptionPane.WARNING_MESSAGE);
    }

    public void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, Formato.html(mensagem, 420), "Erro", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Pede confirmação com botões que dizem o que vai acontecer (ex.: "Excluir"
     * e "Cancelar"), em vez de "Sim" e "Não". Cancelar é o padrão.
     */
    public boolean confirmar(String titulo, String mensagem, String textoAcao, boolean destrutivo) {
        Object[] opcoes = {textoAcao, "Cancelar"};
        int escolha = JOptionPane.showOptionDialog(this, Formato.html(mensagem, 380), titulo,
                JOptionPane.DEFAULT_OPTION,
                destrutivo ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE,
                null, opcoes, opcoes[1]);
        return escolha == 0;
    }
}
