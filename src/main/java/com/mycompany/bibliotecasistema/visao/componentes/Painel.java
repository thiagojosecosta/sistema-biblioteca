package com.mycompany.bibliotecasistema.visao.componentes;

/** Uma das telas do menu lateral; responde aos atalhos globais. */
public interface Painel {

    /** Ctrl+N: a ação principal da tela (ex.: novo livro). */
    void acionarPrincipal();

    /** Ctrl+F e ao abrir a tela: coloca o cursor na busca. */
    void focarBusca();
}
