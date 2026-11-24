package com.mycompany.bibliotecasistema;

import com.mycompany.bibliotecasistema.controle.PrincipalControle;
import com.mycompany.bibliotecasistema.modelo.Biblioteca;
import com.mycompany.bibliotecasistema.persistencia.EmprestimoDAO;
import com.mycompany.bibliotecasistema.persistencia.LivroDAO;
import com.mycompany.bibliotecasistema.persistencia.UsuarioDAO;
import com.mycompany.bibliotecasistema.visao.TelaPrincipal;
import com.mycompany.bibliotecasistema.visao.componentes.Tema;
import javax.swing.SwingUtilities;

public class BibliotecaSistema {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tema.aplicar();
            Biblioteca biblioteca = new Biblioteca(new LivroDAO(), new UsuarioDAO(), new EmprestimoDAO());
            TelaPrincipal tela = new TelaPrincipal();
            new PrincipalControle(biblioteca, tela).iniciar();
        });
    }
}
