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
import br.com.conectaeventos.model.Contratante;
import br.com.conectaeventos.utils.SessaoUtils;
import br.com.conectaeventos.utils.ValidadorUtils;

/**
 * Controller responsável pela edição do perfil de um Contratante.
 * Permite visualizar os dados atuais via GET e atualizar os dados cadastrais (nome, e-mail, telefone, endereço, cidade) via POST.
 * Responde exclusivamente em formato JSON.
 */
@WebServlet(name = "EditarPerfilContratanteController", urlPatterns = { "/editarPerfilContratante", "/EditarPerfilContratanteController" })
public class EditarPerfilContratanteController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ContratanteDAO contratanteDAO;

	@Override
	public void init() throws ServletException {
		super.init();
		this.contratanteDAO = new ContratanteDAO();
	}

	/**
	 * Retorna os dados atuais do perfil do Contratante para exibição no formulário de edição.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		Contratante contratante = obterContratanteDaRequisicao(request);

		if (contratante == null) {
			enviarRespostaJson(response, HttpServletResponse.SC_UNAUTHORIZED, false,
					"Acesso não autorizado. Nenhum contratante logado ou identificado.");
			return;
		}

		String jsonPerfil = String.format(
				"{\"sucesso\": true, \"perfil\": {\"id_contratante\": %d, \"nome_contratante\": \"%s\", \"email_contratante\": \"%s\", "
						+ "\"cpf_cnpj\": \"%s\", \"telefone\": \"%s\", \"endereco\": \"%s\", \"cidade\": \"%s\", \"situacao\": \"%s\", \"data_cadastro\": \"%s\"}}",
				contratante.getId_contratante(),
				escaparJson(contratante.getNome_contratante()),
				escaparJson(contratante.getEmail_contratante()),
				escaparJson(contratante.getCpf_cnpj()),
				escaparJson(contratante.getTelefone()),
				escaparJson(contratante.getEndereco()),
				escaparJson(contratante.getCidade()),
				escaparJson(contratante.getSituacao()),
				contratante.getData_cadastro() != null ? contratante.getData_cadastro().toString() : ""
		);

		enviarJsonDireto(response, HttpServletResponse.SC_OK, jsonPerfil);
	}

	/**
	 * Processa a atualização dos dados cadastrais do Contratante.
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		Contratante contratante = obterContratanteDaRequisicao(request);

		if (contratante == null) {
			enviarRespostaJson(response, HttpServletResponse.SC_UNAUTHORIZED, false,
					"Acesso não autorizado. Faça login para atualizar seu perfil.");
			return;
		}

		// Obtém parâmetros do formulário
		String nome = obterParametro(request, "nome_contratante", "nome");
		String email = obterParametro(request, "email_contratante", "email");
		String telefone = obterParametro(request, "telefone");
		String endereco = obterParametro(request, "endereco");
		String cidade = obterParametro(request, "cidade");

		// Suporte para payload JSON no corpo da requisição
		String contentType = request.getContentType();
		if (contentType != null && contentType.toLowerCase().contains("application/json") && ValidadorUtils.isVazio(nome)) {
			String jsonBody = lerCorpoRequisicao(request);
			nome = extrairCampoJson(jsonBody, "nome_contratante", "nome");
			email = extrairCampoJson(jsonBody, "email_contratante", "email");
			telefone = extrairCampoJson(jsonBody, "telefone");
			endereco = extrairCampoJson(jsonBody, "endereco");
			cidade = extrairCampoJson(jsonBody, "cidade");
		}

		// Fallback para valores atuais caso algum campo não seja enviado
		if (ValidadorUtils.isVazio(nome)) {
			nome = contratante.getNome_contratante();
		}
		if (ValidadorUtils.isVazio(email)) {
			email = contratante.getEmail_contratante();
		}
		if (ValidadorUtils.isVazio(telefone)) {
			telefone = contratante.getTelefone();
		}
		if (ValidadorUtils.isVazio(endereco)) {
			endereco = contratante.getEndereco();
		}
		if (ValidadorUtils.isVazio(cidade)) {
			cidade = contratante.getCidade();
		}

		// 1. Validação de formato de e-mail se informado
		if (!ValidadorUtils.isVazio(email) && !ValidadorUtils.isEmailValido(email)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "Informe um endereço de e-mail válido.");
			return;
		}

		if (contratanteDAO == null) {
			contratanteDAO = new ContratanteDAO();
		}

		// 2. Validação de e-mail duplicado caso tenha sido alterado
		if (email != null && !email.equalsIgnoreCase(contratante.getEmail_contratante())) {
			Contratante contratanteExistente = contratanteDAO.buscarPorEmail(email);
			if (contratanteExistente != null && !contratanteExistente.getCpf_cnpj().equals(contratante.getCpf_cnpj())) {
				enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false,
						"O e-mail informado já está em uso por outra conta.");
				return;
			}
			contratante.setEmail_contratante(email);
		}

		// 3. Atualiza os dados no objeto Contratante
		contratante.setNome_contratante(nome);
		contratante.setTelefone(telefone);
		contratante.setEndereco(endereco);
		contratante.setCidade(cidade);

		// 4. Persiste as alterações no banco de dados
		boolean atualizado = contratanteDAO.atualizar(contratante);

		if (atualizado) {
			// Atualiza os dados na sessão
			SessaoUtils.salvarSessao(request, SessaoUtils.CHAVE_CONTRATANTE, contratante);
			SessaoUtils.salvarSessao(request, SessaoUtils.CHAVE_USUARIO, contratante);

			// Retorna resposta com os dados atualizados
			String jsonSucesso = String.format(
					"{\"sucesso\": true, \"mensagem\": \"Perfil atualizado com sucesso!\", "
							+ "\"perfil\": {\"id_contratante\": %d, \"nome_contratante\": \"%s\", \"email_contratante\": \"%s\", "
							+ "\"cpf_cnpj\": \"%s\", \"telefone\": \"%s\", \"endereco\": \"%s\", \"cidade\": \"%s\", \"situacao\": \"%s\"}}",
					contratante.getId_contratante(),
					escaparJson(contratante.getNome_contratante()),
					escaparJson(contratante.getEmail_contratante()),
					escaparJson(contratante.getCpf_cnpj()),
					escaparJson(contratante.getTelefone()),
					escaparJson(contratante.getEndereco()),
					escaparJson(contratante.getCidade()),
					escaparJson(contratante.getSituacao())
			);

			enviarJsonDireto(response, HttpServletResponse.SC_OK, jsonSucesso);
		} else {
			enviarRespostaJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false,
					"Erro ao atualizar o perfil no banco de dados. Tente novamente.");
		}
	}

	/**
	 * Identifica o contratante a partir da sessão HTTP ou de um parâmetro identificador (CPF/CNPJ ou Email).
	 */
	private Contratante obterContratanteDaRequisicao(HttpServletRequest request) {
		// Tenta obter da sessão
		Object objetoSessao = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_CONTRATANTE);
		if (objetoSessao == null) {
			objetoSessao = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_USUARIO);
		}

		if (objetoSessao instanceof Contratante) {
			return (Contratante) objetoSessao;
		}

		// Fallback para requisições com identificador direto
		String cpfCnpj = obterParametro(request, "cpf_cnpj", "cpfCnpj", "cpf", "cnpj");
		String email = obterParametro(request, "email_contratante", "email");

		if (contratanteDAO == null) {
			contratanteDAO = new ContratanteDAO();
		}

		if (!ValidadorUtils.isVazio(cpfCnpj)) {
			return contratanteDAO.buscarPorCpfCnpj(cpfCnpj);
		}
		if (!ValidadorUtils.isVazio(email)) {
			return contratanteDAO.buscarPorEmail(email);
		}

		return null;
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
	 * Envia resposta padronizada de mensagem em formato JSON.
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
	 * Envia JSON direto na resposta.
	 */
	private void enviarJsonDireto(HttpServletResponse response, int status, String json) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		response.getWriter().flush();
	}
}
