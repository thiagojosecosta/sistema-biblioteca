package com.mycompany.bibliotecasistema.visao;

import com.mycompany.bibliotecasistema.modelo.Livro;
import com.mycompany.bibliotecasistema.visao.componentes.DialogoFormulario;
import java.awt.Window;
import java.time.Year;
import java.util.function.Consumer;
import javax.swing.JTextField;

/** Formulário para cadastrar ou editar um livro. */
public class DialogoLivro extends DialogoFormulario {

    private static final int ANO_MINIMO = 1450;

    private final JTextField titulo = new JTextField(30);
    private final JTextField autor = new JTextField(30);
    private final JTextField ano = new JTextField(6);
    private final Campo campoTitulo;
    private final Campo campoAutor;
    private final Campo campoAno;
    private final Livro existente;
    private final Consumer<Livro> salvar;
    private Livro resultado;

    public DialogoLivro(Window dono, Livro existente, Consumer<Livro> salvar) {
        super(dono,
                existente == null ? "Novo livro" : "Editar livro",
                existente == null ? "Preencha os dados do livro." : "Altere o que precisar e clique em Salvar.",
                "Salvar");
        this.existente = existente;
        this.salvar = salvar;

        campoTitulo = adicionarCampo("Título", titulo, "Ex.: Dom Casmurro");
        campoAutor = adicionarCampo("Autor", autor, "Ex.: Machado de Assis");
        campoAno = adicionarCampo("Ano", ano, "Ano de publicação, ex.: 1899", false);

        if (existente != null) {
            titulo.setText(existente.getTitulo());
            autor.setText(existente.getAutor());
            ano.setText(String.valueOf(existente.getAno()));
        }
        finalizarLayout();
    }

    @Override
    protected boolean validar() {
        boolean ok = true;
        if (titulo.getText().isBlank()) {
            campoTitulo.erro("Informe o título.");
            ok = false;
        } else if (titulo.getText().strip().length() > 150) {
            campoTitulo.erro("Use no máximo 150 caracteres.");
            ok = false;
        }
        if (autor.getText().isBlank()) {
            campoAutor.erro("Informe o autor.");
            ok = false;
        } else if (autor.getText().strip().length() > 100) {
            campoAutor.erro("Use no máximo 100 caracteres.");
            ok = false;
        }
        int anoAtual = Year.now().getValue();
        try {
            int valor = Integer.parseInt(ano.getText().strip());
            if (valor < ANO_MINIMO || valor > anoAtual) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            campoAno.erro("Informe um ano entre " + ANO_MINIMO + " e " + anoAtual + ", só com números.");
            ok = false;
        }
        return ok;
    }

    @Override
    protected void confirmar() {
        Livro livro = new Livro(titulo.getText().strip(), autor.getText().strip(), Integer.parseInt(ano.getText().strip()));
        if (existente != null) {
            livro.setId(existente.getId());
            livro.setDisponivel(existente.isDisponivel());
        }
        salvar.accept(livro);
        resultado = livro;
    }

    public Livro getResultado() {
        return resultado;
    }
}
