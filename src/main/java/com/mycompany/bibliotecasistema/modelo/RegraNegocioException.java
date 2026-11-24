package com.mycompany.bibliotecasistema.modelo;

/**
 * Uma regra da biblioteca impediu a operação. A mensagem é escrita para o
 * usuário final e pode ser mostrada diretamente na tela.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
