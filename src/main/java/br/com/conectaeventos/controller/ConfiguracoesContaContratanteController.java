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
 * Controller responsável pela gestão das configurações de conta do Contratante.
 * Permite visualizar dados da conta, alterar informações cadastrais, trocar senha e atualizar status/situação.
 * Responde exclusivamente em formato JSON.
 */
@WebServlet(name = "ConfiguracoesContaContratanteController", urlPatterns = { "/configuracoesContaContratante", "/ConfiguracoesContaContratanteController" })
public class ConfiguracoesContaContratanteController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ContratanteDAO contratanteDAO;

	@Override
	public void init() throws ServletException {
		super.init();
		this.contratanteDAO = new ContratanteDAO();
	}

	/**
	 * Retorna as informações atuais da conta do Contratante em formato JSON.
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

		// Retorna os dados da conta em formato JSON
		String jsonDados = String.format(
				"{\"sucesso\": true, \"id_contratante\": %d, \"nome\": \"%s\", \"email\": \"%s\", \"cpf_cnpj\": \"%s\", "
						+ "\"telefone\": \"%s\", \"endereco\": \"%s\", \"cidade\": \"%s\", \"situacao\": \"%s\", \"data_cadastro\": \"%s\"}",
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

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonDados);
		response.getWriter().flush();
	}

	/**
	 * Processa alterações nas configurações da conta do Contratante (dados cadastrais, senha ou situação).
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		Contratante contratante = obterContratanteDaRequisicao(request);
		if (contratante == null) {
			enviarRespostaJson(response, HttpServletResponse.SC_UNAUTHORIZED, false,
					"Acesso não autorizado. Faça login para alterar as configurações de conta.");
			return;
		}

		// Obtém ação solicitada ("alterar_dados", "alterar_senha", "desativar", etc.)
		String acao = obterParametro(request, "acao", "action", "tipo");

		// Se parâmetros vierem no corpo JSON
		String contentType = request.getContentType();
		String jsonBody = "";
		if (contentType != null && contentType.toLowerCase().contains("application/json")) {
			jsonBody = lerCorpoRequisicao(request);
			if (ValidadorUtils.isVazio(acao)) {
				acao = extrairCampoJson(jsonBody, "acao", "action", "tipo");
			}
		}

		if (contratanteDAO == null) {
			contratanteDAO = new ContratanteDAO();
		}

		// Roteamento por ação
		if ("alterar_senha".equalsIgnoreCase(acao)) {
			processarAlteracaoSenha(request, response, contratante, jsonBody);
		} else if ("desativar".equalsIgnoreCase(acao) || "alterar_situacao".equalsIgnoreCase(acao)) {
			processarAlteracaoSituacao(request, response, contratante, jsonBody);
		} else {
			processarAlteracaoDados(request, response, contratante, jsonBody);
		}
	}

	/**
	 * Processa a atualização de dados cadastrais gerais (nome, telefone, endereço, cidade, e-mail).
	 */
	private void processarAlteracaoDados(HttpServletRequest request, HttpServletResponse response,
			Contratante contratante, String jsonBody) throws IOException {

		String nome = obterParametro(request, "nome_contratante", "nome");
		String email = obterParametro(request, "email_contratante", "email");
		String telefone = obterParametro(request, "telefone");
		String endereco = obterParametro(request, "endereco");
		String cidade = obterParametro(request, "cidade");

		if (!jsonBody.isEmpty() && ValidadorUtils.isVazio(nome)) {
			nome = extrairCampoJson(jsonBody, "nome_contratante", "nome");
			email = extrairCampoJson(jsonBody, "email_contratante", "email");
			telefone = extrairCampoJson(jsonBody, "telefone");
			endereco = extrairCampoJson(jsonBody, "endereco");
			cidade = extrairCampoJson(jsonBody, "cidade");
		}

		// Preenche com valores atuais se parâmetro não foi enviado
		if (ValidadorUtils.isVazio(nome)) nome = contratante.getNome_contratante();
		if (ValidadorUtils.isVazio(email)) email = contratante.getEmail_contratante();
		if (ValidadorUtils.isVazio(telefone)) telefone = contratante.getTelefone();
		if (ValidadorUtils.isVazio(endereco)) endereco = contratante.getEndereco();
		if (ValidadorUtils.isVazio(cidade)) cidade = contratante.getCidade();

		// Valida e-mail se foi alterado
		if (!email.equalsIgnoreCase(contratante.getEmail_contratante())) {
			if (!ValidadorUtils.isEmailValido(email)) {
				enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "Endereço de e-mail inválido.");
				return;
			}
			if (contratanteDAO.buscarPorEmail(email) != null) {
				enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "O e-mail informado já pertence a outra conta.");
				return;
			}
			contratante.setEmail_contratante(email);
		}

		contratante.setNome_contratante(nome);
		contratante.setTelefone(telefone);
		contratante.setEndereco(endereco);
		contratante.setCidade(cidade);

		boolean atualizado = contratanteDAO.atualizar(contratante);

		if (atualizado) {
			SessaoUtils.salvarSessao(request, SessaoUtils.CHAVE_CONTRATANTE, contratante);
			enviarRespostaJson(response, HttpServletResponse.SC_OK, true, "Configurações da conta atualizadas com sucesso!");
		} else {
			enviarRespostaJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "Erro ao atualizar as configurações da conta no banco de dados.");
		}
	}

	/**
	 * Processa a alteração de senha do Contratante.
	 */
	private void processarAlteracaoSenha(HttpServletRequest request, HttpServletResponse response,
			Contratante contratante, String jsonBody) throws IOException {

		String senhaAtual = obterParametro(request, "senhaAtual", "senha_atual");
		String novaSenha = obterParametro(request, "novaSenha", "nova_senha");
		String confirmarNovaSenha = obterParametro(request, "confirmarNovaSenha", "confirmar_nova_senha");

		if (!jsonBody.isEmpty() && ValidadorUtils.isVazio(senhaAtual)) {
			senhaAtual = extrairCampoJson(jsonBody, "senhaAtual", "senha_atual");
			novaSenha = extrairCampoJson(jsonBody, "novaSenha", "nova_senha");
			confirmarNovaSenha = extrairCampoJson(jsonBody, "confirmarNovaSenha", "confirmar_nova_senha");
		}

		if (ValidadorUtils.isVazio(senhaAtual) || ValidadorUtils.isVazio(novaSenha)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "Informe a senha atual e a nova senha.");
			return;
		}

		// Verifica se a senha atual confere
		if (!senhaAtual.equals(contratante.getSenha_contratante())) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "A senha atual informada está incorreta.");
			return;
		}

		// Valida confirmação de nova senha se fornecida
		if (!ValidadorUtils.isVazio(confirmarNovaSenha) && !novaSenha.equals(confirmarNovaSenha)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "A nova senha e a confirmação não coincidem.");
			return;
		}

		// Valida tamanho mínimo da nova senha
		if (novaSenha.length() < 6) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "A nova senha deve possuir no mínimo 6 caracteres.");
			return;
		}

		contratante.setSenha_contratante(novaSenha);
		boolean atualizado = contratanteDAO.atualizar(contratante);

		if (atualizado) {
			SessaoUtils.salvarSessao(request, SessaoUtils.CHAVE_CONTRATANTE, contratante);
			enviarRespostaJson(response, HttpServletResponse.SC_OK, true, "Senha alterada com sucesso!");
		} else {
			enviarRespostaJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "Erro ao atualizar a senha no banco de dados.");
		}
	}

	/**
	 * Processa alteração do status/situação da conta (ex: desativação).
	 */
	private void processarAlteracaoSituacao(HttpServletRequest request, HttpServletResponse response,
			Contratante contratante, String jsonBody) throws IOException {

		String novaSituacao = obterParametro(request, "situacao", "status");
		if (!jsonBody.isEmpty() && ValidadorUtils.isVazio(novaSituacao)) {
			novaSituacao = extrairCampoJson(jsonBody, "situacao", "status");
		}

		if (ValidadorUtils.isVazio(novaSituacao)) {
			novaSituacao = "INATIVO";
		}

		contratante.setSituacao(novaSituacao);
		boolean atualizado = contratanteDAO.atualizar(contratante);

		if (atualizado) {
			SessaoUtils.salvarSessao(request, SessaoUtils.CHAVE_CONTRATANTE, contratante);
			enviarRespostaJson(response, HttpServletResponse.SC_OK, true, "Situação da conta alterada para: " + novaSituacao);
		} else {
			enviarRespostaJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "Erro ao atualizar a situação da conta no banco de dados.");
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

		// Se não estiver na sessão, tenta identificar por parâmetro
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
	 * Auxiliar para buscar parâmetros da requisição suportando aliases.
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
	 * Lê o conteúdo do corpo da requisição HTTP (JSON).
	 */
	private String lerCorpoRequisicao(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = request.getReader()) {
			String linha;
			while ((linha = reader.readLine()) != null) {
				sb.append(linha);
			}
		} catch (Exception e) {
			// Corpo indisponível
		}
		return sb.toString();
	}

	/**
	 * Extrai valor de chave em JSON simples.
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
	 * Envia resposta JSON.
	 */
	private void enviarRespostaJson(HttpServletResponse response, int status, boolean sucesso, String mensagem) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		String json = String.format("{\"sucesso\": %b, \"mensagem\": \"%s\"}", sucesso, escaparJson(mensagem));
		response.getWriter().write(json);
		response.getWriter().flush();
	}
}

