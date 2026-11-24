package com.mycompany.bibliotecasistema.visao.componentes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formatação de textos para a tela, no jeito brasileiro. */
public final class Formato {

    private static final Locale BRASIL = Locale.of("pt", "BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter POR_EXTENSO = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", BRASIL);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private Formato() {
    }

    public static String data(LocalDate data) {
        return data == null ? "—" : DATA.format(data);
    }

    public static String dataPorExtenso(LocalDate data) {
        String texto = POR_EXTENSO.format(data);
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    public static String hora(LocalTime hora) {
        return HORA.format(hora);
    }

    /** plural(1, "livro", "livros") = "1 livro"; plural(3, ...) = "3 livros". */
    public static String plural(long quantidade, String singular, String plural) {
        return quantidade + " " + (quantidade == 1 ? singular : plural);
    }

    /** Texto que quebra linha sozinho dentro de um JLabel. */
    public static String html(String texto, int largura) {
        String seguro = texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
        return "<html><div style='width:" + largura + "px'>" + seguro + "</div></html>";
    }
}
