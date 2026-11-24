package com.mycompany.bibliotecasistema.controle;

import com.mycompany.bibliotecasistema.modelo.Biblioteca;
import com.mycompany.bibliotecasistema.modelo.RegraNegocioException;
import com.mycompany.bibliotecasistema.visao.TelaPrincipal;

/** O que todo controle tem: as regras (Biblioteca), a janela e como atualizar as telas. */
abstract class ControleBase {

    protected final Biblioteca biblioteca;
    protected final TelaPrincipal tela;
    protected final Runnable atualizarTudo;

    protected ControleBase(Biblioteca biblioteca, TelaPrincipal tela, Runnable atualizarTudo) {
        this.biblioteca = biblioteca;
        this.tela = tela;
        this.atualizarTudo = atualizarTudo;
    }

    /**
     * Executa a ação mostrando qualquer problema de forma amigável.
     * Devolve true se deu certo.
     */
    protected boolean executar(Runnable acao) {
        try {
            acao.run();
            return true;
        } catch (RegraNegocioException e) {
            tela.mostrarAviso(e.getMessage());
        } catch (RuntimeException e) {
            tela.mostrarErro(e.getMessage());
        }
        return false;
    }
}
