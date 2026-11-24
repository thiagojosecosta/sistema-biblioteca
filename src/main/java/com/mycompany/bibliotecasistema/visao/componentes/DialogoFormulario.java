package com.mycompany.bibliotecasistema.visao.componentes;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Molde dos formulários (novo/editar): título, campos com texto de ajuda e
 * mensagem de erro logo abaixo de cada um, botões Cancelar e confirmar.
 * Enter confirma, Esc cancela. Se der erro ao salvar, a janela continua
 * aberta com o que foi digitado.
 */
public abstract class DialogoFormulario extends JDialog {

    private static final int LARGURA_MENSAGEM = 300;

    private final JPanel formulario = new JPanel(new GridBagLayout());
    private final JLabel erroGeral = new JLabel();
    private final List<Campo> campos = new ArrayList<>();
    protected final JButton botaoConfirmar;
    private int linha;
    private boolean confirmado;

    protected DialogoFormulario(Window dono, String titulo, String subtitulo, String textoConfirmar) {
        super(dono, titulo, ModalityType.APPLICATION_MODAL);

        JLabel rotuloTitulo = new JLabel(titulo);
        Tema.fonte(rotuloTitulo, "h2");
        JLabel rotuloSubtitulo = new JLabel(subtitulo);
        Tema.suave(rotuloSubtitulo);
        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.add(rotuloTitulo);
        topo.add(Box.createVerticalStrut(4));
        topo.add(rotuloSubtitulo);

        botaoConfirmar = new JButton(textoConfirmar);
        Tema.destacar(botaoConfirmar);
        JButton cancelar = new JButton("Cancelar");
        cancelar.setToolTipText("Fechar sem salvar (Esc)");
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botoes.add(cancelar);
        botoes.add(Box.createHorizontalStrut(8));
        botoes.add(botaoConfirmar);

        erroGeral.setForeground(Tema.cor(Tema.Tom.PERIGO));
        JPanel rodape = new JPanel(new BorderLayout(0, 12));
        rodape.add(erroGeral, BorderLayout.NORTH);
        rodape.add(botoes, BorderLayout.SOUTH);

        JPanel conteudo = new JPanel(new BorderLayout(0, 20));
        conteudo.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        conteudo.add(topo, BorderLayout.NORTH);
        conteudo.add(formulario, BorderLayout.CENTER);
        conteudo.add(rodape, BorderLayout.SOUTH);
        setContentPane(conteudo);

        getRootPane().setDefaultButton(botaoConfirmar);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        cancelar.addActionListener(e -> dispose());
        botaoConfirmar.addActionListener(e -> tentarConfirmar());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    protected Campo adicionarCampo(String rotulo, JComponent componente, String ajuda) {
        return adicionarCampo(rotulo, componente, ajuda, true);
    }

    /** Adiciona uma linha: rótulo à esquerda, campo à direita e, embaixo dele, ajuda ou erro. */
    protected Campo adicionarCampo(String rotulo, JComponent componente, String ajuda, boolean esticar) {
        JLabel rotuloCampo = new JLabel(rotulo);
        rotuloCampo.setLabelFor(componente);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = linha;
        g.anchor = GridBagConstraints.LINE_START;
        g.insets = new Insets(0, 0, 0, 16);
        formulario.add(rotuloCampo, g);

        g = new GridBagConstraints();
        g.gridx = 1;
        g.gridy = linha;
        g.weightx = 1;
        g.anchor = GridBagConstraints.LINE_START;
        g.fill = esticar ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        formulario.add(componente, g);

        JLabel mensagem = new JLabel();
        g = new GridBagConstraints();
        g.gridx = 1;
        g.gridy = linha + 1;
        g.anchor = GridBagConstraints.LINE_START;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 0, 14, 0);
        formulario.add(mensagem, g);

        linha += 2;
        Campo campo = new Campo(rotuloCampo, componente, mensagem, ajuda);
        campos.add(campo);
        campo.limpar();
        return campo;
    }

    /** Chamar no fim do construtor da subclasse. */
    protected void finalizarLayout() {
        pack();
        setMinimumSize(new Dimension(Math.max(getWidth(), 480), getHeight()));
        setLocationRelativeTo(getOwner());
    }

    /** Confere os campos e marca os erros. Devolve true se está tudo certo. */
    protected abstract boolean validar();

    /** Salva. Pode lançar exceção com mensagem para o usuário; a janela continua aberta. */
    protected abstract void confirmar();

    private void tentarConfirmar() {
        erroGeral.setText("");
        campos.forEach(Campo::limpar);
        if (!validar()) {
            campos.stream().filter(c -> c.comErro).findFirst()
                    .ifPresent(c -> c.componente.requestFocusInWindow());
            pack();
            return;
        }
        try {
            confirmar();
            confirmado = true;
            dispose();
        } catch (RuntimeException e) {
            erroGeral.setText(Formato.html(e.getMessage(), LARGURA_MENSAGEM + 100));
            pack();
        }
    }

    /** Abre a janela e espera. Devolve true se o usuário salvou. */
    public boolean exibir() {
        setVisible(true);
        return confirmado;
    }

    /** Um campo do formulário com seu rótulo e a linha de mensagem abaixo. */
    public static final class Campo {

        private final JLabel rotulo;
        private final JComponent componente;
        private final JLabel mensagem;
        private String ajuda;
        private boolean comErro;

        private Campo(JLabel rotulo, JComponent componente, JLabel mensagem, String ajuda) {
            this.rotulo = rotulo;
            this.componente = componente;
            this.mensagem = mensagem;
            this.ajuda = ajuda;
        }

        public void erro(String texto) {
            comErro = true;
            mensagem.setText(Formato.html(texto, LARGURA_MENSAGEM));
            mensagem.setForeground(Tema.cor(Tema.Tom.PERIGO));
            componente.putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);
        }

        public void limpar() {
            comErro = false;
            mensagem.setText(ajuda == null || ajuda.isBlank() ? " " : Formato.html(ajuda, LARGURA_MENSAGEM));
            mensagem.setForeground(UIManager.getColor("Label.disabledForeground"));
            componente.putClientProperty(FlatClientProperties.OUTLINE, null);
        }

        public void setAjuda(String ajuda) {
            this.ajuda = ajuda;
            if (!comErro) {
                limpar();
            }
        }

        public void setRotulo(String texto) {
            rotulo.setText(texto);
        }
    }
}
