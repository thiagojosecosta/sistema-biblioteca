package com.mycompany.bibliotecasistema.visao;

import com.mycompany.bibliotecasistema.modelo.Aluno;
import com.mycompany.bibliotecasistema.modelo.Professor;
import com.mycompany.bibliotecasistema.modelo.Usuario;
import com.mycompany.bibliotecasistema.visao.componentes.Formato;
import com.mycompany.bibliotecasistema.visao.componentes.PainelLista;
import com.mycompany.bibliotecasistema.visao.componentes.TabelaPadrao.Coluna;
import com.mycompany.bibliotecasistema.visao.componentes.Tema.Tom;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JComboBox;

public class PainelUsuarios extends PainelLista<PainelUsuarios.Linha> {

    /** Um usuário e como ele está com os empréstimos. */
    public record Linha(Usuario usuario, int emAberto, boolean comAtraso) {

        boolean podePegarLivro() {
            return !comAtraso && emAberto < usuario.getLimiteEmprestimos();
        }

        String situacao() {
            if (comAtraso) {
                return "Com atraso";
            }
            return emAberto >= usuario.getLimiteEmprestimos() ? "No limite" : "Em dia";
        }
    }

    private static final String TODOS = "Todos os usuários";
    private static final String ALUNOS = "Só alunos";
    private static final String PROFESSORES = "Só professores";

    private final JComboBox<String> filtro = new JComboBox<>(new String[]{TODOS, ALUNOS, PROFESSORES});
    private final Acao editar;
    private final Acao excluir;
    private final Acao verEmprestimos;
    private final Acao emprestar;

    public PainelUsuarios() {
        super("Usuários", "Buscar por nome, matrícula, curso ou departamento", List.of(
                Coluna.<Linha>numero("ID", 50, l -> l.usuario().getId()),
                Coluna.<Linha>texto("Nome", 220, l -> l.usuario().getNome()),
                Coluna.<Linha>texto("Matrícula", 100, l -> l.usuario().getMatricula()),
                Coluna.<Linha>texto("Tipo", 90, l -> l.usuario().getTipo()),
                Coluna.<Linha>texto("Curso / Departamento", 200, l -> l.usuario().getVinculo()),
                Coluna.<Linha>texto("Livros com ele", 110,
                        l -> l.emAberto() + " de " + l.usuario().getLimiteEmprestimos()),
                Coluna.<Linha>situacao("Situação", 120, Linha::situacao, texto -> switch (texto) {
                    case "Com atraso" -> Tom.PERIGO;
                    case "No limite" -> Tom.ALERTA;
                    default -> Tom.SUCESSO;
                })),
                l -> l.usuario().getId(),
                l -> l.usuario().getNome() + " " + l.usuario().getMatricula() + " " + l.usuario().getVinculo());

        definirAcaoPrincipal("+ Novo usuário", "Cadastrar aluno ou professor (Ctrl+N)");
        tabela.setMensagemVazia("Nenhum usuário cadastrado. Clique em “+ Novo usuário” para começar.");

        filtro.addActionListener(e -> setFiltroExtra(switch ((String) filtro.getSelectedItem()) {
            case ALUNOS -> l -> l.usuario() instanceof Aluno;
            case PROFESSORES -> l -> l.usuario() instanceof Professor;
            default -> l -> true;
        }));
        adicionarFiltro(filtro);

        verEmprestimos = criarAcao("Ver empréstimos", "Ver o histórico de empréstimos deste usuário", l -> true);
        emprestar = criarAcao("Emprestar livro",
                "Registrar um empréstimo para este usuário (não disponível com atraso ou no limite)",
                Linha::podePegarLivro);
        editar = criarAcao("Editar", "Editar o usuário selecionado (Enter ou duplo clique)", l -> true);
        excluir = criarAcao("Excluir", "Excluir o usuário (Delete). Só é possível sem livros emprestados.",
                l -> l.emAberto() == 0);
    }

    @Override
    protected String resumir(List<Linha> linhas) {
        long alunos = linhas.stream().filter(l -> l.usuario() instanceof Aluno).count();
        long comAtraso = linhas.stream().filter(Linha::comAtraso).count();
        String texto = Formato.plural(linhas.size(), "usuário", "usuários")
                + "  ·  " + Formato.plural(alunos, "aluno", "alunos")
                + "  ·  " + Formato.plural(linhas.size() - alunos, "professor", "professores");
        if (comAtraso > 0) {
            texto += "  ·  " + Formato.plural(comAtraso, "com atraso", "com atraso");
        }
        return texto;
    }

    public void aoEditar(Consumer<Usuario> acao) {
        editar.aoExecutar(l -> acao.accept(l.usuario()));
        tabela.aoAbrir(l -> acao.accept(l.usuario()));
    }

    public void aoExcluir(Consumer<Usuario> acao) {
        excluir.aoExecutar(l -> acao.accept(l.usuario()));
        tabela.aoExcluir(l -> acao.accept(l.usuario()));
    }

    public void aoVerEmprestimos(Consumer<Usuario> acao) {
        verEmprestimos.aoExecutar(l -> acao.accept(l.usuario()));
    }

    public void aoEmprestar(Consumer<Usuario> acao) {
        emprestar.aoExecutar(l -> acao.accept(l.usuario()));
    }
}
