package br.com.conectaeventos.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.conectaeventos.dao.ContratanteDAO;
import br.com.conectaeventos.dao.PrestadorDAO;
import br.com.conectaeventos.model.Contratante;
import br.com.conectaeventos.model.Prestador;
import br.com.conectaeventos.utils.SessaoUtils;
import br.com.conectaeventos.utils.ValidadorUtils;

/**
 * Controller responsável pelo login e logout de usuários (Contratantes e Prestadores).
 * Atua como uma API REST, retornando respostas em formato JSON e gerenciando a sessão HTTP.
 */
@WebServlet(name = "LoginController", urlPatterns = { "/login", "/LoginController", "/logout" })
public class LoginController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ContratanteDAO contratanteDAO;
	private PrestadorDAO prestadorDAO;

	@Override
	public void init() throws ServletException {
		super.init();
		this.contratanteDAO = new ContratanteDAO();
		this.prestadorDAO = new PrestadorDAO();
	}

	/**
	 * Verifica o status da sessão do usuário autenticado no sistema.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		// Verifica se a requisição é para logout via GET
		String uri = request.getRequestURI();
		String acao = obterParametro(request, "acao", "action");
		if ((uri != null && uri.endsWith("/logout")) || "logout".equalsIgnoreCase(acao)) {
			SessaoUtils.encerrarSessao(request);
			enviarRespostaJson(response, HttpServletResponse.SC_OK, true, "Logout realizado com sucesso!");
			return;
		}

		// Verifica contratante na sessão
		Object objetoContratante = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_CONTRATANTE);
		if (objetoContratante instanceof Contratante) {
			Contratante c = (Contratante) objetoContratante;
			String json = String.format(
					"{\"sucesso\": true, \"autenticado\": true, \"tipo\": \"contratante\", "
							+ "\"usuario\": {\"id\": %d, \"nome\": \"%s\", \"email\": \"%s\", \"cpf_cnpj\": \"%s\", \"cidade\": \"%s\", \"situacao\": \"%s\"}}",
					c.getId_contratante(),
					escaparJson(c.getNome_contratante()),
					escaparJson(c.getEmail_contratante()),
					escaparJson(c.getCpf_cnpj()),
					escaparJson(c.getCidade()),
					escaparJson(c.getSituacao())
			);
			enviarJsonDireto(response, HttpServletResponse.SC_OK, json);
			return;
		}

		// Verifica prestador na sessão
		Object objetoPrestador = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_PRESTADOR);
		if (objetoPrestador instanceof Prestador) {
			Prestador p = (Prestador) objetoPrestador;
			String json = String.format(
					"{\"sucesso\": true, \"autenticado\": true, \"tipo\": \"prestador\", "
							+ "\"usuario\": {\"id\": %d, \"nome\": \"%s\", \"email\": \"%s\", \"cpf_cnpj\": \"%s\", \"categoria\": \"%s\", \"cidade\": \"%s\", \"situacao\": \"%s\"}}",
					p.getId_prestador(),
					escaparJson(p.getNome_prestador()),
					escaparJson(p.getEmail_prestador()),
					escaparJson(p.getCpf_cnpj()),
					escaparJson(p.getCategoria()),
					escaparJson(p.getCidade()),
					escaparJson(p.getSituacao())
			);
			enviarJsonDireto(response, HttpServletResponse.SC_OK, json);
			return;
		}

		// Nenhum usuário autenticado
		enviarRespostaJson(response, HttpServletResponse.SC_OK, false, "Nenhum usuário autenticado.");
	}

	/**
	 * Processa a autenticação de login ou logout.
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		// Verifica se é uma requisição de logout
		String uri = request.getRequestURI();
		String acao = obterParametro(request, "acao", "action");
		if ((uri != null && uri.endsWith("/logout")) || "logout".equalsIgnoreCase(acao)) {
			SessaoUtils.encerrarSessao(request);
			enviarRespostaJson(response, HttpServletResponse.SC_OK, true, "Logout realizado com sucesso!");
			return;
		}

		// Obtém credenciais dos parâmetros do formulário
		String email = obterParametro(request, "email", "email_usuario", "login");
		String senha = obterParametro(request, "senha", "senha_usuario", "password");

		// Se vier via JSON no corpo da requisição
		String contentType = request.getContentType();
		if (contentType != null && contentType.toLowerCase().contains("application/json") && ValidadorUtils.isVazio(email)) {
			String jsonBody = lerCorpoRequisicao(request);
			email = extrairCampoJson(jsonBody, "email", "email_usuario", "login");
			senha = extrairCampoJson(jsonBody, "senha", "senha_usuario", "password");
			if (ValidadorUtils.isVazio(acao)) {
				acao = extrairCampoJson(jsonBody, "acao", "action");
				if ("logout".equalsIgnoreCase(acao)) {
					SessaoUtils.encerrarSessao(request);
					enviarRespostaJson(response, HttpServletResponse.SC_OK, true, "Logout realizado com sucesso!");
					return;
				}
			}
		}

		// 1. Validação de campos obrigatórios
		if (ValidadorUtils.isVazio(email) || ValidadorUtils.isVazio(senha)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "E-mail e senha são obrigatórios.");
			return;
		}

		// 2. Validação básica de formato de e-mail
		if (!ValidadorUtils.isEmailValido(email)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "Informe um formato de e-mail válido.");
			return;
		}

		// Inicializa DAOs se necessário
		if (contratanteDAO == null) {
			contratanteDAO = new ContratanteDAO();
		}
		if (prestadorDAO == null) {
			prestadorDAO = new PrestadorDAO();
		}

		// 3. Tenta autenticar primeiro como Contratante
		Contratante contratante = contratanteDAO.autenticar(email, senha);
		if (contratante != null) {
			if ("INATIVO".equalsIgnoreCase(contratante.getSituacao())) {
				enviarRespostaJson(response, HttpServletResponse.SC_FORBIDDEN, false,
						"Sua conta de contratante está inativa. Entre em contato com o suporte.");
				return;
			}

			// Salva contratante na sessão
			SessaoUtils.salvarSessao(request, SessaoUtils.CHAVE_CONTRATANTE, contratante);
			SessaoUtils.salvarSessao(request, SessaoUtils.CHAVE_USUARIO, contratante);

			String jsonSucesso = String.format(
					"{\"sucesso\": true, \"mensagem\": \"Login realizado com sucesso!\", \"tipo\": \"contratante\", "
							+ "\"usuario\": {\"id\": %d, \"nome\": \"%s\", \"email\": \"%s\", \"cpf_cnpj\": \"%s\", \"cidade\": \"%s\", \"situacao\": \"%s\"}}",
					contratante.getId_contratante(),
					escaparJson(contratante.getNome_contratante()),
					escaparJson(contratante.getEmail_contratante()),
					escaparJson(contratante.getCpf_cnpj()),
					escaparJson(contratante.getCidade()),
					escaparJson(contratante.getSituacao())
			);
			enviarJsonDireto(response, HttpServletResponse.SC_OK, jsonSucesso);
			return;
		}

		// 4. Tenta autenticar como Prestador de Serviços
		Prestador prestador = prestadorDAO.autenticar(email, senha);
		if (prestador != null) {
			if ("INATIVO".equalsIgnoreCase(prestador.getSituacao())) {
				enviarRespostaJson(response, HttpServletResponse.SC_FORBIDDEN, false,
						"Sua conta de prestador está inativa. Entre em contato com o suporte.");
				return;
			}

			// Salva prestador na sessão
			SessaoUtils.salvarSessao(request, SessaoUtils.CHAVE_PRESTADOR, prestador);
			SessaoUtils.salvarSessao(request, SessaoUtils.CHAVE_USUARIO, prestador);

			String jsonSucesso = String.format(
					"{\"sucesso\": true, \"mensagem\": \"Login realizado com sucesso!\", \"tipo\": \"prestador\", "
							+ "\"usuario\": {\"id\": %d, \"nome\": \"%s\", \"email\": \"%s\", \"cpf_cnpj\": \"%s\", \"categoria\": \"%s\", \"cidade\": \"%s\", \"situacao\": \"%s\"}}",
					prestador.getId_prestador(),
					escaparJson(prestador.getNome_prestador()),
					escaparJson(prestador.getEmail_prestador()),
					escaparJson(prestador.getCpf_cnpj()),
					escaparJson(prestador.getCategoria()),
					escaparJson(prestador.getCidade()),
					escaparJson(prestador.getSituacao())
			);
			enviarJsonDireto(response, HttpServletResponse.SC_OK, jsonSucesso);
			return;
		}

		// 5. Credenciais inválidas
		enviarRespostaJson(response, HttpServletResponse.SC_UNAUTHORIZED, false, "E-mail ou senha inválidos.");
	}

	/**
	 * Auxiliar para buscar parâmetros da requisição suportando diferentes nomes de campos.
	 */
	private String obterParametro(HttpServletRequest request, String... nomes) {
		for (String nome : nomes) {
			String valor = request.getParameter(nome);
			if (valor != null && !valor.trim().isEmpty()) {
				return valor.trim();
			}
		}
		return "";
	}

	/**
	 * Lê o corpo da requisição em formato String.
	 */
	private String lerCorpoRequisicao(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = request.getReader()) {
			String linha;
			while ((linha = reader.readLine()) != null) {
				sb.append(linha);
			}
		} catch (Exception e) {
			// Leitura vazia
		}
		return sb.toString();
	}

	/**
	 * Extrai o valor de um campo de uma string JSON simples via expressão regular.
	 */
	private String extrairCampoJson(String json, String... chaves) {
		if (json == null || json.isEmpty()) {
			return "";
		}
		for (String chave : chaves) {
			String padrao = "\"" + chave + "\"\\s*:\\s*\"([^\"]*)\"";
			Pattern pattern = Pattern.compile(padrao);
			Matcher matcher = pattern.matcher(json);
			if (matcher.find()) {
				return matcher.group(1).trim();
			}
		}
		return "";
	}

	/**
	 * Escapa aspas e barras para strings JSON.
	 */
	private String escaparJson(String texto) {
		if (texto == null) {
			return "";
		}
		return texto.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	/**
	 * Envia resposta padronizada em formato JSON.
	 */
	private void enviarRespostaJson(HttpServletResponse response, int status, boolean sucesso, String mensagem)
			throws IOException {
		response.setStatus(status);
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		String json = String.format("{\"sucesso\": %b, \"mensagem\": \"%s\"}", sucesso, escaparJson(mensagem));
		response.getWriter().write(json);
		response.getWriter().flush();
	}

	/**
	 * Envia JSON pronto diretamente na resposta.
	 */
	private void enviarJsonDireto(HttpServletResponse response, int status, String json) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		response.getWriter().flush();
	}
}
