package br.com.conectaeventos.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.conectaeventos.dao.PrestadorDAO;
import br.com.conectaeventos.model.Prestador;
import br.com.conectaeventos.utils.ValidadorUtils;

/**
 * Controller responsável pelo processo de cadastro de um novo Prestador de Serviços.
 * Atua como uma API HTTP/REST, enviando respostas em formato JSON.
 */
@WebServlet(name = "CadastroPrestadorController", urlPatterns = { "/cadastroPrestador", "/CadastroPrestadorController" })
public class CadastroPrestadorController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private PrestadorDAO prestadorDAO;

	@Override
	public void init() throws ServletException {
		super.init();
		this.prestadorDAO = new PrestadorDAO();
	}

	/**
	 * Retorna informações sobre o endpoint em formato JSON.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		enviarRespostaJson(response, HttpServletResponse.SC_OK, true,
				"Endpoint de cadastro de Prestador ativo. Envie uma requisição POST com os dados para realizar o cadastro.");
	}

	/**
	 * Processa o cadastro do prestador recebendo dados via Form ou JSON e retornando resposta JSON.
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		// Tenta obter parâmetros do formulário tradicional (form-urlencoded)
		String nome = obterParametro(request, "nome_prestador", "nome");
		String email = obterParametro(request, "email_prestador", "email");
		String senha = obterParametro(request, "senha_prestador", "senha");
		String confirmarSenha = obterParametro(request, "confirmarSenha", "confirmar_senha", "senha_confirmacao");
		String cpfCnpj = obterParametro(request, "cpf_cnpj", "cpfCnpj", "cpf", "cnpj");
		String telefone = obterParametro(request, "telefone");
		String endereco = obterParametro(request, "endereco");
		String cidade = obterParametro(request, "cidade");
		String categoria = obterParametro(request, "categoria", "categoria_servico", "ramo");
		String descricao = obterParametro(request, "descricao", "descricao_servico");

		// Se os parâmetros do formulário estiverem vazios e o Content-Type for JSON
		String contentType = request.getContentType();
		if (contentType != null && contentType.toLowerCase().contains("application/json") && ValidadorUtils.isVazio(nome)) {
			String jsonBody = lerCorpoRequisicao(request);
			nome = extrairCampoJson(jsonBody, "nome_prestador", "nome");
			email = extrairCampoJson(jsonBody, "email_prestador", "email");
			senha = extrairCampoJson(jsonBody, "senha_prestador", "senha");
			confirmarSenha = extrairCampoJson(jsonBody, "confirmarSenha", "confirmar_senha", "senha_confirmacao");
			cpfCnpj = extrairCampoJson(jsonBody, "cpf_cnpj", "cpfCnpj", "cpf", "cnpj");
			telefone = extrairCampoJson(jsonBody, "telefone");
			endereco = extrairCampoJson(jsonBody, "endereco");
			cidade = extrairCampoJson(jsonBody, "cidade");
			categoria = extrairCampoJson(jsonBody, "categoria", "categoria_servico", "ramo");
			descricao = extrairCampoJson(jsonBody, "descricao", "descricao_servico");
		}

		// 1. Validação de campos obrigatórios
		if (ValidadorUtils.isVazio(nome) || ValidadorUtils.isVazio(email) || ValidadorUtils.isVazio(senha)
				|| ValidadorUtils.isVazio(cpfCnpj) || ValidadorUtils.isVazio(telefone)
				|| ValidadorUtils.isVazio(endereco) || ValidadorUtils.isVazio(cidade)) {

			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "Preencha todos os campos obrigatórios.");
			return;
		}

		// 2. Validação do formato de e-mail
		if (!ValidadorUtils.isEmailValido(email)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "Informe um endereço de e-mail válido.");
			return;
		}

		// 3. Validação de confirmação de senha
		if (!ValidadorUtils.isVazio(confirmarSenha) && !senha.equals(confirmarSenha)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "A senha e a confirmação de senha não coincidem.");
			return;
		}

		// 4. Validação de tamanho mínimo de senha
		if (senha.length() < 6) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "A senha deve conter no mínimo 6 caracteres.");
			return;
		}

		// 5. Validação do CPF ou CNPJ
		String cpfCnpjNumerico = ValidadorUtils.apenasNumeros(cpfCnpj);
		if (!ValidadorUtils.isCpfOuCnpjValido(cpfCnpj)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "CPF ou CNPJ inválido. Informe um documento válido com 11 (CPF) ou 14 (CNPJ) dígitos.");
			return;
		}

		// Garante inicialização do DAO
		if (prestadorDAO == null) {
			prestadorDAO = new PrestadorDAO();
		}

		// 6. Verifica duplicidade de e-mail no banco de dados
		if (prestadorDAO.buscarPorEmail(email) != null) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "O e-mail informado já está cadastrado no sistema.");
			return;
		}

		// 7. Verifica duplicidade de CPF/CNPJ no banco de dados
		if (prestadorDAO.buscarPorCpfCnpj(cpfCnpj) != null || prestadorDAO.buscarPorCpfCnpj(cpfCnpjNumerico) != null) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "O CPF/CNPJ informado já está cadastrado no sistema.");
			return;
		}

		// 8. Instancia o objeto Prestador
		Date dataAtual = new Date(System.currentTimeMillis());
		String situacaoPadrao = "ATIVO";

		Prestador prestador = new Prestador();
		prestador.setNome_prestador(nome);
		prestador.setEmail_prestador(email);
		prestador.setSenha_prestador(senha);
		prestador.setCpf_cnpj(cpfCnpjNumerico.isEmpty() ? cpfCnpj : cpfCnpjNumerico);
		prestador.setTelefone(telefone);
		prestador.setEndereco(endereco);
		prestador.setCidade(cidade);
		prestador.setCategoria(categoria);
		prestador.setDescricao(descricao);
		prestador.setData_cadastro(dataAtual);
		prestador.setSituacao(situacaoPadrao);

		// 9. Persiste os dados via DAO
		boolean sucesso = prestadorDAO.cadastrar(prestador);

		if (sucesso) {
			enviarRespostaJson(response, HttpServletResponse.SC_CREATED, true, "Cadastro de prestador realizado com sucesso!");
		} else {
			enviarRespostaJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "Erro ao realizar o cadastro no banco de dados. Tente novamente.");
		}
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
	 * Lê o conteúdo em formato String do corpo da requisição HTTP (útil para payloads JSON).
	 */
	private String lerCorpoRequisicao(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = request.getReader()) {
			String linha;
			while ((linha = reader.readLine()) != null) {
				sb.append(linha);
			}
		} catch (Exception e) {
			// Leitura do corpo vazia ou indisponível
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
	 * Envia a resposta HTTP no formato JSON.
	 */
	private void enviarRespostaJson(HttpServletResponse response, int status, boolean sucesso, String mensagem) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		String mensagemEscapada = mensagem != null ? mensagem.replace("\\", "\\\\").replace("\"", "\\\"") : "";

		String json = String.format("{\"sucesso\": %b, \"mensagem\": \"%s\"}", sucesso, mensagemEscapada);
		response.getWriter().write(json);
		response.getWriter().flush();
	}
}

