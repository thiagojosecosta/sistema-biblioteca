package com.mycompany.bibliotecasistema.visao.componentes;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/** Cartão de resumo da tela inicial: um número grande e o que ele significa. */
public class Cartao extends JPanel {

    private final JLabel numero = new JLabel("–");
    private final JLabel descricao;

    public Cartao(String descricao) {
        super(new BorderLayout(0, 2));
        this.descricao = new JLabel(descricao);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        Tema.fonte(numero, "h00");
        Tema.suave(this.descricao);
        add(numero, BorderLayout.NORTH);
        add(this.descricao, BorderLayout.CENTER);
    }

    /** Mostra o valor; com um tom (ex.: PERIGO para atrasados) o número ganha cor. */
    public void setValor(long valor, Tema.Tom tom) {
        numero.setText(String.valueOf(valor));
        numero.setForeground(tom == null ? UIManager.getColor("Label.foreground") : Tema.cor(tom));
    }

    /** O cartão vira um atalho: clicar leva para a lista correspondente. */
    public void aoClicar(String dica, Runnable acao) {
        setToolTipText(dica);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                acao.run();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UIManager.getColor("Table.background"));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.setColor(UIManager.getColor("Component.borderColor"));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.dispose();
        super.paintComponent(g);
    }
}
