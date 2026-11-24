package com.mycompany.bibliotecasistema.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Emprestimo {

    private int id;
    private Livro livro;
    private Usuario usuario;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevolucao;

    public Emprestimo() {
    }

    public Emprestimo(Livro livro, Usuario usuario, LocalDate dataEmprestimo, LocalDate dataDevolucaoPrevista) {
        this.livro = livro;
        this.usuario = usuario;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public LocalDate getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public void setDataDevolucaoPrevista(LocalDate dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    /** O livro ainda não foi devolvido. */
    public boolean isAberto() {
        return dataDevolucao == null;
    }

    public boolean isAtrasado(LocalDate hoje) {
        return isAberto() && hoje.isAfter(dataDevolucaoPrevista);
    }

    /** Dias de atraso até hoje (se aberto) ou até a data em que foi devolvido. */
    public long getDiasAtraso(LocalDate hoje) {
        LocalDate fim = isAberto() ? hoje : dataDevolucao;
        return Math.max(0, ChronoUnit.DAYS.between(dataDevolucaoPrevista, fim));
    }

    /** Situação em linguagem do dia a dia, ex.: "Vence em 3 dias", "Atrasado há 2 dias". */
    public String getSituacao(LocalDate hoje) {
        if (!isAberto()) {
            return getDiasAtraso(hoje) > 0 ? "Devolvido com atraso" : "Devolvido";
        }
        long dias = ChronoUnit.DAYS.between(hoje, dataDevolucaoPrevista);
        if (dias < 0) {
            return "Atrasado há " + dias(-dias);
        }
        if (dias == 0) {
            return "Vence hoje";
        }
        if (dias == 1) {
            return "Vence amanhã";
        }
        return "Vence em " + dias(dias);
    }

    private static String dias(long quantidade) {
        return quantidade + (quantidade == 1 ? " dia" : " dias");
    }
}
