package com.mycompany.bibliotecasistema.visao.componentes;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.UIManager;
import javax.swing.border.Border;

/** Linha fina de separação que acompanha o tema (claro/escuro). */
public class BordaLinha implements Border {

    public enum Lado {
        TOPO, DIREITA
    }

    private final Lado lado;
    private final Insets espaco;

    public BordaLinha(Lado lado, int topo, int esquerda, int baixo, int direita) {
        this.lado = lado;
        this.espaco = new Insets(topo, esquerda, baixo, direita);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int largura, int altura) {
        g.setColor(UIManager.getColor("Component.borderColor"));
        if (lado == Lado.TOPO) {
            g.fillRect(x, y, largura, 1);
        } else {
            g.fillRect(x + largura - 1, y, 1, altura);
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return (Insets) espaco.clone();
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
