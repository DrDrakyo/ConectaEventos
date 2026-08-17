package br.com.conectaeventos.utils;

/**
 * Validações reutilizadas pelos Controllers deste módulo (Buscar Prestadores,
 * Avaliar Prestador, Acompanhar Contratação). Contém apenas o que é
 * efetivamente usado por essas telas.
 */
public class ValidadorUtils {

    private ValidadorUtils() {
        // classe utilitária: não deve ser instanciada
    }

    /**
     * Usado em avaliar-prestador.html: a nota é obrigatória e deve estar entre 1 e 5.
     */
    public static boolean notaValida(Integer nota) {
        return nota != null && nota >= 1 && nota <= 5;
    }

    /**
     * Campo de texto obrigatório (ex: comentário, quando exigido) não pode
     * vir nulo/vazio.
     */
    public static boolean textoPreenchido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Usado nos filtros numéricos de buscar-prestadores.html (preço máximo,
     * reputação mínima), que chegam como String via request.getParameter.
     * Retorna true se o valor for um número válido e não-negativo, ou se
     * vier vazio (filtro não aplicado é sempre válido).
     */
    public static boolean numeroOpcionalValido(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return true;
        }
        try {
            double numero = Double.parseDouble(valor.trim());
            return numero >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}