package com.mycompany.bibliotecasistema.visao.componentes;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatSearchIcon;
import java.util.function.Consumer;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Campo de busca com lupa, texto de exemplo e botão para limpar. Filtra enquanto digita. */
public class CampoBusca extends JTextField {

    public CampoBusca(String dica) {
        super(28);
        putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, dica);
        putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSearchIcon());
        putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
    }

    public void aoDigitar(Consumer<String> acao) {
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                acao.accept(getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                acao.accept(getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                acao.accept(getText());
            }
        });
    }
}
