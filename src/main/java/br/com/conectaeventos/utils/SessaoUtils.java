package br.com.conectaeventos.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Helper para leitura de dados de sessão do usuário logado.
 * Usado pelos Controllers que exigem contratante autenticado
 * (Avaliar Prestador, Acompanhar Contratação).
 */
public class SessaoUtils {

    public static final String ATRIBUTO_TIPO_USUARIO = "tipoUsuario";
    public static final String ATRIBUTO_CPF_CNPJ_CONTRATANTE = "cpfCnpjContratante";

    private SessaoUtils() {
        // classe utilitária: não deve ser instanciada
    }

    /**
     * Verifica se existe um contratante autenticado na sessão atual.
     */
    public static boolean isContratanteLogado(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        return sessao != null && "CONTRATANTE".equals(sessao.getAttribute(ATRIBUTO_TIPO_USUARIO));
    }

    /**
     * Retorna o cpf_cnpj do contratante logado, ou null se não houver sessão válida.
     */
    public static String getCpfCnpjContratante(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao == null) {
            return null;
        }
        return (String) sessao.getAttribute(ATRIBUTO_CPF_CNPJ_CONTRATANTE);
    }
}