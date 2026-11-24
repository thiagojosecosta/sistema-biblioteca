package com.mycompany.bibliotecasistema.visao.componentes;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.UIManager;

/** Aparência do sistema (FlatLaf), tema claro/escuro e cores de situação. */
public final class Tema {

    /** Significado de uma cor; o texto ao lado sempre diz a mesma coisa (não dependemos só da cor). */
    public enum Tom {
        SUCESSO, ALERTA, PERIGO, NEUTRO
    }

    private static final Preferences PREFERENCIAS = Preferences.userNodeForPackage(Tema.class);
    private static boolean escuro;

    private Tema() {
    }

    public static void aplicar() {
        escuro = PREFERENCIAS.getBoolean("escuro", false);
        configurar();
    }

    public static void alternar() {
        escuro = !escuro;
        PREFERENCIAS.putBoolean("escuro", escuro);
        configurar();
        FlatLaf.updateUI();
    }

    public static boolean isEscuro() {
        return escuro;
    }

    private static void configurar() {
        FlatLaf.setGlobalExtraDefaults(Map.of("@accentColor", "#2563EB"));
        if (escuro) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
        UIManager.put("Component.arc", 8);
        UIManager.put("Button.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Table.rowHeight", 34);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
        UIManager.put("TableHeader.height", 34);
        UIManager.put("ScrollPane.smoothScrolling", true);
    }

    public static Color cor(Tom tom) {
        return switch (tom) {
            case SUCESSO -> escuro ? new Color(0x4ADE80) : new Color(0x15803D);
            case ALERTA -> escuro ? new Color(0xFBBF24) : new Color(0xB45309);
            case PERIGO -> escuro ? new Color(0xF87171) : new Color(0xB91C1C);
            case NEUTRO -> UIManager.getColor("Label.disabledForeground");
        };
    }

    /** Destaca o botão da ação mais importante da tela. */
    public static void destacar(JButton botao) {
        botao.putClientProperty(FlatClientProperties.STYLE,
                "background: $Component.accentColor; foreground: #FFFFFF; borderWidth: 0; focusedBorderColor: #FFFFFF;"
                + "hoverBackground: darken($Component.accentColor,8%); pressedBackground: darken($Component.accentColor,15%);"
                + "disabledBackground: fade($Component.accentColor,40%); disabledText: #FFFFFF;"
                + "font: bold; margin: 6,14,6,14");
    }

    /** Texto secundário, em cinza. */
    public static void suave(javax.swing.JComponent componente) {
        componente.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground");
    }

    /** Aplica um estilo de fonte do FlatLaf: "h1", "h2", "h3", "h00", "large", "small"... */
    public static void fonte(javax.swing.JComponent componente, String estilo) {
        componente.putClientProperty(FlatClientProperties.STYLE_CLASS, estilo);
    }
}
