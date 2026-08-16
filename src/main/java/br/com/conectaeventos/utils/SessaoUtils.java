package br.com.conectaeventos.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessaoUtils {

	public static final String CHAVE_CONTRATANTE = "contratanteLogado";
	public static final String CHAVE_PRESTADOR = "prestadorLogado";
	public static final String CHAVE_USUARIO = "usuarioLogado";

	/**
	 * Salva o objeto do usuário na sessão HTTP.
	 */
	public static void salvarSessao(HttpServletRequest request, String chave, Object usuario) {
		HttpSession session = request.getSession(true);
		session.setAttribute(chave, usuario);
	}

	/**
	 * Obtém um atributo da sessão HTTP.
	 */
	public static Object obterDaSessao(HttpServletRequest request, String chave) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			return session.getAttribute(chave);
		}
		return null;
	}

	/**
	 * Encerra e invalida a sessão HTTP atual.
	 */
	public static void encerrarSessao(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}
}

