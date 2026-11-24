package com.mycompany.bibliotecasistema.visao;

import com.mycompany.bibliotecasistema.modelo.Aluno;
import com.mycompany.bibliotecasistema.modelo.Professor;
import com.mycompany.bibliotecasistema.modelo.Usuario;
import com.mycompany.bibliotecasistema.visao.componentes.DialogoFormulario;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/** Formulário para cadastrar ou editar um aluno ou professor. */
public class DialogoUsuario extends DialogoFormulario {

    private final JRadioButton aluno = new JRadioButton("Aluno", true);
    private final JRadioButton professor = new JRadioButton("Professor");
    private final JTextField nome = new JTextField(30);
    private final JTextField matricula = new JTextField(12);
    private final JTextField vinculo = new JTextField(30);
    private final Campo campoTipo;
    private final Campo campoNome;
    private final Campo campoMatricula;
    private final Campo campoVinculo;
    private final Usuario existente;
    private final Consumer<Usuario> salvar;
    private Usuario resultado;

    public DialogoUsuario(Window dono, Usuario existente, Consumer<Usuario> salvar) {
        super(dono,
                existente == null ? "Novo usuário" : "Editar usuário",
                existente == null ? "Cadastre um aluno ou professor." : "Altere o que precisar e clique em Salvar.",
                "Salvar");
        this.existente = existente;
        this.salvar = salvar;

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(aluno);
        grupo.add(professor);
        JPanel tipos = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tipos.add(aluno);
        tipos.add(Box.createHorizontalStrut(16));
        tipos.add(professor);

        campoTipo = adicionarCampo("Tipo", tipos, "");
        campoNome = adicionarCampo("Nome", nome, "Nome completo");
        campoMatricula = adicionarCampo("Matrícula", matricula, "Ex.: 2024001 ou P0001", false);
        campoVinculo = adicionarCampo("Curso", vinculo, "");

        if (existente != null) {
            professor.setSelected(existente instanceof Professor);
            nome.setText(existente.getNome());
            matricula.setText(existente.getMatricula());
            vinculo.setText(existente.getVinculo());
        }
        aluno.addActionListener(e -> atualizarTipo());
        professor.addActionListener(e -> atualizarTipo());
        atualizarTipo();
        finalizarLayout();
    }

    /** Ajusta o formulário ao tipo escolhido e explica as regras de empréstimo dele. */
    private void atualizarTipo() {
        if (aluno.isSelected()) {
            campoTipo.setAjuda("Alunos pegam até " + Aluno.LIMITE_EMPRESTIMOS + " livros, por "
                    + Aluno.PRAZO_DIAS + " dias cada.");
            campoVinculo.setRotulo("Curso");
            campoVinculo.setAjuda("Ex.: Sistemas de Informação");
        } else {
            campoTipo.setAjuda("Professores pegam até " + Professor.LIMITE_EMPRESTIMOS + " livros, por "
                    + Professor.PRAZO_DIAS + " dias cada.");
            campoVinculo.setRotulo("Departamento");
            campoVinculo.setAjuda("Ex.: Computação");
        }
    }

    @Override
    protected boolean validar() {
        boolean ok = obrigatorio(campoNome, nome, "Informe o nome.", 100);
        ok &= obrigatorio(campoMatricula, matricula, "Informe a matrícula.", 20);
        if (!matricula.getText().isBlank() && matricula.getText().strip().contains(" ")) {
            campoMatricula.erro("A matrícula não pode ter espaços.");
            ok = false;
        }
        ok &= obrigatorio(campoVinculo, vinculo,
                aluno.isSelected() ? "Informe o curso." : "Informe o departamento.", 100);
        return ok;
    }

    private static boolean obrigatorio(Campo campo, JTextField texto, String mensagem, int maximo) {
        if (texto.getText().isBlank()) {
            campo.erro(mensagem);
            return false;
        }
        if (texto.getText().strip().length() > maximo) {
            campo.erro("Use no máximo " + maximo + " caracteres.");
            return false;
        }
        return true;
    }

    @Override
    protected void confirmar() {
        String n = nome.getText().strip();
        String m = matricula.getText().strip();
        String v = vinculo.getText().strip();
        Usuario usuario = aluno.isSelected() ? new Aluno(n, m, v) : new Professor(n, m, v);
        if (existente != null) {
            usuario.setId(existente.getId());
        }
        salvar.accept(usuario);
        resultado = usuario;
    }

    public Usuario getResultado() {
        return resultado;
    }
}
