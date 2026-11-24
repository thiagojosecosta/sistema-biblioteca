package com.mycompany.bibliotecasistema.visao;

import com.mycompany.bibliotecasistema.modelo.Emprestimo;
import com.mycompany.bibliotecasistema.visao.componentes.Cartao;
import com.mycompany.bibliotecasistema.visao.componentes.Formato;
import com.mycompany.bibliotecasistema.visao.componentes.Painel;
import com.mycompany.bibliotecasistema.visao.componentes.TabelaPadrao;
import com.mycompany.bibliotecasistema.visao.componentes.TabelaPadrao.Coluna;
import com.mycompany.bibliotecasistema.visao.componentes.Tema;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/** Tela inicial: um resumo do dia e as devoluções mais urgentes. */
public class PainelInicio extends JPanel implements Painel {

    private final JLabel data = new JLabel(" ");
    private final Cartao livros = new Cartao("Livros no acervo");
    private final Cartao disponiveis = new Cartao("Disponíveis para empréstimo");
    private final Cartao abertos = new Cartao("Empréstimos em aberto");
    private final Cartao atrasados = new Cartao("Atrasados");
    private final TabelaPadrao<Emprestimo> proximas;
    private final JButton novoEmprestimo = new JButton("+ Novo empréstimo");
    private final JButton devolver = new JButton("Registrar devolução");
    private Consumer<Emprestimo> aoDevolver = e -> {
    };

    public PainelInicio() {
        super(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 16, 28));

        JLabel titulo = new JLabel("Início");
        Tema.fonte(titulo, "h1");
        Tema.suave(data);
        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(data);

        Tema.destacar(novoEmprestimo);
        novoEmprestimo.setToolTipText("Registrar um empréstimo (Ctrl+N)");
        JPanel areaPrincipal = new JPanel(new GridBagLayout());
        areaPrincipal.add(novoEmprestimo);

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.add(textos, BorderLayout.WEST);
        cabecalho.add(areaPrincipal, BorderLayout.EAST);

        JPanel cartoes = new JPanel(new GridLayout(1, 4, 16, 0));
        cartoes.add(livros);
        cartoes.add(disponiveis);
        cartoes.add(abertos);
        cartoes.add(atrasados);

        JPanel topo = new JPanel(new BorderLayout(0, 20));
        topo.add(cabecalho, BorderLayout.NORTH);
        topo.add(cartoes, BorderLayout.SOUTH);

        proximas = new TabelaPadrao<>(List.of(
                Coluna.<Emprestimo>texto("Livro", 280, e -> e.getLivro().getTitulo()),
                Coluna.<Emprestimo>texto("Usuário", 200, e -> e.getUsuario().getNome()),
                Coluna.<Emprestimo>data("Devolver até", 120, Emprestimo::getDataDevolucaoPrevista),
                Coluna.<Emprestimo>situacao("Situação", 170, e -> e.getSituacao(LocalDate.now()),
                        PainelEmprestimos::tomDaSituacao)),
                Emprestimo::getId);
        proximas.setMensagemVazia("Nenhum livro emprestado no momento.");
        proximas.aoAbrir(e -> aoDevolver.accept(e));
        proximas.aoMudarSelecao(() -> devolver.setEnabled(proximas.getSelecionado() != null));

        JLabel tituloLista = new JLabel("Próximas devoluções");
        Tema.fonte(tituloLista, "h3");
        JLabel dicaLista = new JLabel("Livros emprestados, do mais urgente para o menos urgente.");
        Tema.suave(dicaLista);
        JPanel textosLista = new JPanel();
        textosLista.setLayout(new BoxLayout(textosLista, BoxLayout.Y_AXIS));
        textosLista.add(tituloLista);
        textosLista.add(dicaLista);

        JPanel lista = new JPanel(new BorderLayout(0, 10));
        lista.add(textosLista, BorderLayout.NORTH);
        lista.add(new JScrollPane(proximas), BorderLayout.CENTER);

        devolver.setEnabled(false);
        devolver.setToolTipText("Registrar a devolução do empréstimo selecionado (Enter ou duplo clique)");
        devolver.addActionListener(e -> {
            Emprestimo selecionado = proximas.getSelecionado();
            if (selecionado != null) {
                aoDevolver.accept(selecionado);
            }
        });
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acoes.add(devolver);

        add(topo, BorderLayout.NORTH);
        add(lista, BorderLayout.CENTER);
        add(acoes, BorderLayout.SOUTH);
    }

    public void mostrar(int totalLivros, long livrosDisponiveis, List<Emprestimo> emAberto) {
        LocalDate hoje = LocalDate.now();
        long qtdAtrasados = emAberto.stream().filter(e -> e.isAtrasado(hoje)).count();
        data.setText(Formato.dataPorExtenso(hoje));
        livros.setValor(totalLivros, null);
        disponiveis.setValor(livrosDisponiveis, null);
        abertos.setValor(emAberto.size(), null);
        atrasados.setValor(qtdAtrasados, qtdAtrasados > 0 ? Tema.Tom.PERIGO : Tema.Tom.SUCESSO);
        proximas.setDados(emAberto);
        devolver.setEnabled(proximas.getSelecionado() != null);
    }

    public void aoNovoEmprestimo(Runnable acao) {
        novoEmprestimo.addActionListener(e -> acao.run());
    }

    public void aoDevolver(Consumer<Emprestimo> acao) {
        this.aoDevolver = acao;
    }

    public void aoClicarLivros(Runnable acao) {
        livros.aoClicar("Ver todos os livros", acao);
    }

    public void aoClicarDisponiveis(Runnable acao) {
        disponiveis.aoClicar("Ver os livros disponíveis", acao);
    }

    public void aoClicarAbertos(Runnable acao) {
        abertos.aoClicar("Ver os empréstimos em aberto", acao);
    }

    public void aoClicarAtrasados(Runnable acao) {
        atrasados.aoClicar("Ver os empréstimos atrasados", acao);
    }

    @Override
    public void acionarPrincipal() {
        novoEmprestimo.doClick();
    }

    @Override
    public void focarBusca() {
        proximas.requestFocusInWindow();
    }
}
