package br.com.conectaeventos.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.conectaeventos.dao.AvaliacaoDAO;
import br.com.conectaeventos.dao.ContratacaoDAO;
import br.com.conectaeventos.dao.PortfolioDAO;
import br.com.conectaeventos.dao.PrestadorDAO;
import br.com.conectaeventos.model.Avaliacao;
import br.com.conectaeventos.model.PortfolioItem;
import br.com.conectaeventos.model.Prestador;
import br.com.conectaeventos.utils.SessaoUtils;
import br.com.conectaeventos.utils.ValidadorUtils;

/**
 * Controller responsável pela visualização e edição do perfil de um prestador de serviços.
 * Retorna dados detalhados do prestador, métricas de contratações concluídas, nota média,
 * avaliações recebidas e galeria de portfólio.
 */
@WebServlet(name = "PerfilPrestadorController", urlPatterns = { "/perfilPrestador", "/PerfilPrestadorController" })
public class PerfilPrestadorController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private PrestadorDAO prestadorDAO;
	private AvaliacaoDAO avaliacaoDAO;
	private ContratacaoDAO contratacaoDAO;
	private PortfolioDAO portfolioDAO;

	@Override
	public void init() throws ServletException {
		super.init();
		this.prestadorDAO = new PrestadorDAO();
		this.avaliacaoDAO = new AvaliacaoDAO();
		this.contratacaoDAO = new ContratacaoDAO();
		this.portfolioDAO = new PortfolioDAO();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		Prestador prestador = obterPrestadorDaRequisicao(request);

		if (prestador == null) {
			enviarRespostaJson(response, HttpServletResponse.SC_NOT_FOUND, false,
					"Prestador não encontrado ou não autenticado.");
			return;
		}

		if (avaliacaoDAO == null) avaliacaoDAO = new AvaliacaoDAO();
		if (contratacaoDAO == null) contratacaoDAO = new ContratacaoDAO();
		if (portfolioDAO == null) portfolioDAO = new PortfolioDAO();

		double mediaAvaliacoes = avaliacaoDAO.calcularMediaPrestador(prestador.getCpf_cnpj());
		int totalAvaliacoes = avaliacaoDAO.contarAvaliacoesPrestador(prestador.getCpf_cnpj());
		int totalConcluidas = contratacaoDAO.contarPorPrestadorEStatus(prestador.getCpf_cnpj(), "CONCLUIDO");
		List<Avaliacao> avaliacoes = avaliacaoDAO.listarPorPrestador(prestador.getCpf_cnpj());
		List<PortfolioItem> portfolio = portfolioDAO.listarPorPrestador(prestador.getCpf_cnpj());

		StringBuilder json = new StringBuilder();
		json.append("{")
			.append("\"sucesso\": true, ")
			.append("\"prestador\": {")
			.append("\"id_prestador\": ").append(prestador.getId_prestador()).append(", ")
			.append("\"nome_prestador\": \"").append(escaparJson(prestador.getNome_prestador())).append("\", ")
			.append("\"email_prestador\": \"").append(escaparJson(prestador.getEmail_prestador())).append("\", ")
			.append("\"cpf_cnpj\": \"").append(escaparJson(prestador.getCpf_cnpj())).append("\", ")
			.append("\"telefone\": \"").append(escaparJson(prestador.getTelefone())).append("\", ")
			.append("\"endereco\": \"").append(escaparJson(prestador.getEndereco())).append("\", ")
			.append("\"cidade\": \"").append(escaparJson(prestador.getCidade())).append("\", ")
			.append("\"categoria\": \"").append(escaparJson(prestador.getCategoria())).append("\", ")
			.append("\"descricao\": \"").append(escaparJson(prestador.getDescricao())).append("\", ")
			.append("\"data_cadastro\": \"").append(prestador.getData_cadastro() != null ? prestador.getData_cadastro().toString() : "").append("\", ")
			.append("\"situacao\": \"").append(escaparJson(prestador.getSituacao())).append("\", ")
			.append("\"media_avaliacoes\": ").append(String.format(java.util.Locale.US, "%.1f", mediaAvaliacoes)).append(", ")
			.append("\"total_avaliacoes\": ").append(totalAvaliacoes).append(", ")
			.append("\"total_concluidas\": ").append(totalConcluidas)
			.append("}, ")
			.append("\"avaliacoes\": [");

		for (int i = 0; i < avaliacoes.size(); i++) {
			Avaliacao a = avaliacoes.get(i);
			json.append("{")
				.append("\"id_avaliacao\": ").append(a.getId_avaliacao()).append(", ")
				.append("\"id_contratacao\": ").append(a.getId_contratacao()).append(", ")
				.append("\"cpf_cnpj_contratante\": \"").append(escaparJson(a.getCpf_cnpj_contratante())).append("\", ")
				.append("\"nota\": ").append(a.getNota()).append(", ")
				.append("\"comentario\": \"").append(escaparJson(a.getComentario())).append("\", ")
				.append("\"data_avaliacao\": \"").append(a.getData_avaliacao() != null ? a.getData_avaliacao().toString() : "").append("\"")
				.append("}");
			if (i < avaliacoes.size() - 1) {
				json.append(", ");
			}
		}

		json.append("], ")
			.append("\"portfolio\": [");

		for (int i = 0; i < portfolio.size(); i++) {
			PortfolioItem p = portfolio.get(i);
			json.append("{")
				.append("\"id_portfolio\": ").append(p.getId_portfolio()).append(", ")
				.append("\"titulo\": \"").append(escaparJson(p.getTitulo())).append("\", ")
				.append("\"descricao\": \"").append(escaparJson(p.getDescricao())).append("\", ")
				.append("\"imagem_url\": \"").append(escaparJson(p.getImagem_url())).append("\", ")
				.append("\"data_publicacao\": \"").append(p.getData_publicacao() != null ? p.getData_publicacao().toString() : "").append("\"")
				.append("}");
			if (i < portfolio.size() - 1) {
				json.append(", ");
			}
		}

		json.append("]}");

		enviarJsonDireto(response, HttpServletResponse.SC_OK, json.toString());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		// Identifica o prestador logado
		Object objSessao = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_PRESTADOR);
		if (objSessao == null) {
			objSessao = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_USUARIO);
		}

		Prestador prestador = null;
		if (objSessao instanceof Prestador) {
			prestador = (Prestador) objSessao;
		} else {
			String cpfCnpj = obterParametro(request, "cpf_cnpj", "cpfCnpj", "cpf", "cnpj");
			if (!ValidadorUtils.isVazio(cpfCnpj)) {
				prestador = prestadorDAO.buscarPorCpfCnpj(cpfCnpj);
			}
		}

		if (prestador == null) {
			enviarRespostaJson(response, HttpServletResponse.SC_UNAUTHORIZED, false,
					"Acesso não autorizado. Faça login como prestador para atualizar seu perfil.");
			return;
		}

		String nome = obterParametro(request, "nome_prestador", "nome");
		String email = obterParametro(request, "email_prestador", "email");
		String telefone = obterParametro(request, "telefone");
		String endereco = obterParametro(request, "endereco");
		String cidade = obterParametro(request, "cidade");
		String categoria = obterParametro(request, "categoria");
		String descricao = obterParametro(request, "descricao");

		String contentType = request.getContentType();
		if (contentType != null && contentType.toLowerCase().contains("application/json") && ValidadorUtils.isVazio(nome)) {
			String jsonBody = lerCorpoRequisicao(request);
			nome = extrairCampoJson(jsonBody, "nome_prestador", "nome");
			email = extrairCampoJson(jsonBody, "email_prestador", "email");
			telefone = extrairCampoJson(jsonBody, "telefone");
			endereco = extrairCampoJson(jsonBody, "endereco");
			cidade = extrairCampoJson(jsonBody, "cidade");
			categoria = extrairCampoJson(jsonBody, "categoria");
			descricao = extrairCampoJson(jsonBody, "descricao");
		}

		if (ValidadorUtils.isVazio(nome)) nome = prestador.getNome_prestador();
		if (ValidadorUtils.isVazio(email)) email = prestador.getEmail_prestador();
		if (ValidadorUtils.isVazio(telefone)) telefone = prestador.getTelefone();
		if (ValidadorUtils.isVazio(endereco)) endereco = prestador.getEndereco();
		if (ValidadorUtils.isVazio(cidade)) cidade = prestador.getCidade();
		if (ValidadorUtils.isVazio(categoria)) categoria = prestador.getCategoria();
		if (ValidadorUtils.isVazio(descricao)) descricao = prestador.getDescricao();

		if (!ValidadorUtils.isVazio(email) && !ValidadorUtils.isEmailValido(email)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "Informe um endereço de e-mail válido.");
			return;
		}

		if (email != null && !email.equalsIgnoreCase(prestador.getEmail_prestador())) {
			Prestador existente = prestadorDAO.buscarPorEmail(email);
			if (existente != null && !existente.getCpf_cnpj().equals(prestador.getCpf_cnpj())) {
				enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false,
						"O e-mail informado já está em uso por outro prestador.");
				return;
			}
			prestador.setEmail_prestador(email);
		}

		prestador.setNome_prestador(nome);
		prestador.setTelefone(telefone);
		prestador.setEndereco(endereco);
		prestador.setCidade(cidade);
		prestador.setCategoria(categoria);
		prestador.setDescricao(descricao);

		boolean atualizado = prestadorDAO.atualizar(prestador);

		if (atualizado) {
			SessaoUtils.salvarSessao(request, SessaoUtils.CHAVE_PRESTADOR, prestador);
			SessaoUtils.salvarSessao(request, SessaoUtils.CHAVE_USUARIO, prestador);

			enviarRespostaJson(response, HttpServletResponse.SC_OK, true, "Perfil de prestador atualizado com sucesso!");
		} else {
			enviarRespostaJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false,
					"Erro ao atualizar o perfil de prestador no banco de dados.");
		}
	}

	private Prestador obterPrestadorDaRequisicao(HttpServletRequest request) {
		String idParam = obterParametro(request, "id", "id_prestador");
		String cpfCnpj = obterParametro(request, "cpf_cnpj", "cpfCnpj", "cpf", "cnpj");
		String email = obterParametro(request, "email_prestador", "email");

		if (prestadorDAO == null) prestadorDAO = new PrestadorDAO();

		if (!ValidadorUtils.isVazio(idParam)) {
			try {
				int id = Integer.parseInt(idParam);
				return prestadorDAO.buscarPorId(id);
			} catch (NumberFormatException e) {
				// Continua fallback
			}
		}

		if (!ValidadorUtils.isVazio(cpfCnpj)) {
			return prestadorDAO.buscarPorCpfCnpj(cpfCnpj);
		}

		if (!ValidadorUtils.isVazio(email)) {
			return prestadorDAO.buscarPorEmail(email);
		}

		// Fallback para sessão
		Object objSessao = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_PRESTADOR);
		if (objSessao == null) {
			objSessao = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_USUARIO);
		}

		if (objSessao instanceof Prestador) {
			return (Prestador) objSessao;
		}

		return null;
	}

	private String obterParametro(HttpServletRequest request, String... nomes) {
		for (String nome : nomes) {
			String valor = request.getParameter(nome);
			if (valor != null && !valor.trim().isEmpty()) {
				return valor.trim();
			}
		}
		return "";
	}

	private String lerCorpoRequisicao(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = request.getReader()) {
			String linha;
			while ((linha = reader.readLine()) != null) {
				sb.append(linha);
			}
		} catch (Exception e) {
			// Ignora
		}
		return sb.toString();
	}

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

	private String escaparJson(String texto) {
		if (texto == null) {
			return "";
		}
		return texto.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private void enviarRespostaJson(HttpServletResponse response, int status, boolean sucesso, String mensagem)
			throws IOException {
		response.setStatus(status);
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		String json = String.format("{\"sucesso\": %b, \"mensagem\": \"%s\"}", sucesso, escaparJson(mensagem));
		response.getWriter().write(json);
		response.getWriter().flush();
	}

	private void enviarJsonDireto(HttpServletResponse response, int status, String json) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		response.getWriter().flush();
	}
}
