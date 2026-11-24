package com.mycompany.bibliotecasistema.visao.componentes;

import java.awt.BorderLayout;
import java.time.LocalTime;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/** Rodapé que confirma o que acabou de acontecer ("Livro salvo") e quando os dados foram atualizados. */
public class BarraStatus extends JPanel {

    private static final String PRONTO = "Pronto.";

    private final JLabel mensagem = new JLabel(PRONTO);
    private final JLabel atualizado = new JLabel(" ");
    private final Timer limpar = new Timer(8000, e -> mensagem.setText(PRONTO));

    public BarraStatus() {
        super(new BorderLayout());
        setBorder(new BordaLinha(BordaLinha.Lado.TOPO, 6, 16, 6, 16));
        Tema.suave(atualizado);
        add(mensagem, BorderLayout.WEST);
        add(atualizado, BorderLayout.EAST);
        limpar.setRepeats(false);
    }

    public void mostrar(String texto) {
        mensagem.setText(texto);
        limpar.restart();
    }

    public void marcarAtualizado() {
        atualizado.setText("Dados atualizados às " + Formato.hora(LocalTime.now()) + "  ·  F5 para atualizar");
    }
}
