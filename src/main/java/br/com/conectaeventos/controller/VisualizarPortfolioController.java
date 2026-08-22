package br.com.conectaeventos.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.conectaeventos.dao.PortfolioDAO;
import br.com.conectaeventos.dao.PrestadorDAO;
import br.com.conectaeventos.model.PortfolioItem;
import br.com.conectaeventos.model.Prestador;
import br.com.conectaeventos.utils.SessaoUtils;
import br.com.conectaeventos.utils.ValidadorUtils;

/**
 * Controller responsável pela gestão e visualização da galeria/portfólio de um prestador de serviços.
 * Suporta consulta de itens de portfólio (GET) e inclusão/edição/exclusão de itens (POST).
 */
@WebServlet(name = "VisualizarPortfolioController", urlPatterns = { "/visualizarPortfolio", "/VisualizarPortfolioController" })
public class VisualizarPortfolioController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private PortfolioDAO portfolioDAO;
	private PrestadorDAO prestadorDAO;

	@Override
	public void init() throws ServletException {
		super.init();
		this.portfolioDAO = new PortfolioDAO();
		this.prestadorDAO = new PrestadorDAO();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String idPortfolioParam = obterParametro(request, "id", "id_portfolio");
		String cpfCnpjPrestador = obterParametro(request, "cpf_cnpj", "cpf_cnpj_prestador", "prestador");

		if (portfolioDAO == null) portfolioDAO = new PortfolioDAO();
		if (prestadorDAO == null) prestadorDAO = new PrestadorDAO();

		// Se forneceu ID do item específico
		if (!ValidadorUtils.isVazio(idPortfolioParam)) {
			try {
				int idPortfolio = Integer.parseInt(idPortfolioParam);
				PortfolioItem item = portfolioDAO.buscarPorId(idPortfolio);
				if (item != null) {
					String json = String.format(
							"{\"sucesso\": true, \"item\": {\"id_portfolio\": %d, \"cpf_cnpj_prestador\": \"%s\", \"titulo\": \"%s\", \"descricao\": \"%s\", \"imagem_url\": \"%s\", \"data_publicacao\": \"%s\"}}",
							item.getId_portfolio(), item.getCpf_cnpj_prestador(), escaparJson(item.getTitulo()),
							escaparJson(item.getDescricao()), escaparJson(item.getImagem_url()),
							item.getData_publicacao() != null ? item.getData_publicacao().toString() : "");
					enviarJsonDireto(response, HttpServletResponse.SC_OK, json);
				} else {
					enviarRespostaJson(response, HttpServletResponse.SC_NOT_FOUND, false, "Item de portfólio não encontrado.");
				}
				return;
			} catch (NumberFormatException e) {
				enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "ID de portfólio inválido.");
				return;
			}
		}

		// Fallback para CPF/CNPJ da sessão caso não informado
		if (ValidadorUtils.isVazio(cpfCnpjPrestador)) {
			Object obj = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_PRESTADOR);
			if (obj instanceof Prestador) {
				cpfCnpjPrestador = ((Prestador) obj).getCpf_cnpj();
			}
		}

		if (ValidadorUtils.isVazio(cpfCnpjPrestador)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false,
					"Informe 'cpf_cnpj' do prestador ou 'id' do item.");
			return;
		}

		List<PortfolioItem> lista = portfolioDAO.listarPorPrestador(cpfCnpjPrestador);

		StringBuilder json = new StringBuilder();
		json.append("{")
			.append("\"sucesso\": true, ")
			.append("\"cpf_cnpj_prestador\": \"").append(escaparJson(cpfCnpjPrestador)).append("\", ")
			.append("\"total_itens\": ").append(lista.size()).append(", ")
			.append("\"itens\": [");

		for (int i = 0; i < lista.size(); i++) {
			PortfolioItem p = lista.get(i);
			json.append("{")
				.append("\"id_portfolio\": ").append(p.getId_portfolio()).append(", ")
				.append("\"titulo\": \"").append(escaparJson(p.getTitulo())).append("\", ")
				.append("\"descricao\": \"").append(escaparJson(p.getDescricao())).append("\", ")
				.append("\"imagem_url\": \"").append(escaparJson(p.getImagem_url())).append("\", ")
				.append("\"data_publicacao\": \"").append(p.getData_publicacao() != null ? p.getData_publicacao().toString() : "").append("\"")
				.append("}");
			if (i < lista.size() - 1) {
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

		String acao = obterParametro(request, "acao", "action");
		String titulo = obterParametro(request, "titulo");
		String descricao = obterParametro(request, "descricao");
		String imagemUrl = obterParametro(request, "imagem_url", "imagem", "url");
		String idPortfolioParam = obterParametro(request, "id", "id_portfolio");
		String cpfCnpj = obterParametro(request, "cpf_cnpj", "cpf_cnpj_prestador");

		String contentType = request.getContentType();
		if (contentType != null && contentType.toLowerCase().contains("application/json") && ValidadorUtils.isVazio(titulo)
				&& ValidadorUtils.isVazio(acao)) {
			String jsonBody = lerCorpoRequisicao(request);
			acao = extrairCampoJson(jsonBody, "acao", "action");
			titulo = extrairCampoJson(jsonBody, "titulo");
			descricao = extrairCampoJson(jsonBody, "descricao");
			imagemUrl = extrairCampoJson(jsonBody, "imagem_url", "imagem", "url");
			idPortfolioParam = extrairCampoJson(jsonBody, "id", "id_portfolio");
			cpfCnpj = extrairCampoJson(jsonBody, "cpf_cnpj", "cpf_cnpj_prestador");
		}

		// Identifica o prestador autor
		if (ValidadorUtils.isVazio(cpfCnpj)) {
			Object obj = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_PRESTADOR);
			if (obj instanceof Prestador) {
				cpfCnpj = ((Prestador) obj).getCpf_cnpj();
			}
		}

		if (ValidadorUtils.isVazio(cpfCnpj)) {
			enviarRespostaJson(response, HttpServletResponse.SC_UNAUTHORIZED, false,
					"Acesso não autorizado. Faça login como prestador para gerenciar seu portfólio.");
			return;
		}

		if (portfolioDAO == null) portfolioDAO = new PortfolioDAO();

		// Caso: Deletar item
		if ("deletar".equalsIgnoreCase(acao) || "excluir".equalsIgnoreCase(acao)) {
			if (ValidadorUtils.isVazio(idPortfolioParam)) {
				enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "ID do item é obrigatório para exclusão.");
				return;
			}
			try {
				int idPortfolio = Integer.parseInt(idPortfolioParam);
				boolean deletado = portfolioDAO.deletar(idPortfolio);
				if (deletado) {
					enviarRespostaJson(response, HttpServletResponse.SC_OK, true, "Item de portfólio excluído com sucesso!");
				} else {
					enviarRespostaJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "Erro ao excluir item de portfólio.");
				}
				return;
			} catch (NumberFormatException e) {
				enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "ID do item inválido.");
				return;
			}
		}

		// Valida campos obrigatórios para inclusão/edição
		if (ValidadorUtils.isVazio(titulo)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "O título do item é obrigatório.");
			return;
		}

		// Caso: Atualizar item existente
		if ("atualizar".equalsIgnoreCase(acao) || "editar".equalsIgnoreCase(acao)) {
			if (ValidadorUtils.isVazio(idPortfolioParam)) {
				enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "ID do item é obrigatório para atualização.");
				return;
			}
			try {
				int idPortfolio = Integer.parseInt(idPortfolioParam);
				PortfolioItem item = new PortfolioItem(idPortfolio, cpfCnpj, titulo, descricao, imagemUrl, new Date(System.currentTimeMillis()));
				boolean atualizado = portfolioDAO.atualizar(item);
				if (atualizado) {
					enviarRespostaJson(response, HttpServletResponse.SC_OK, true, "Item de portfólio atualizado com sucesso!");
				} else {
					enviarRespostaJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "Erro ao atualizar item de portfólio.");
				}
				return;
			} catch (NumberFormatException e) {
				enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "ID do item inválido.");
				return;
			}
		}

		// Caso padrão: Adicionar novo item
		PortfolioItem novoItem = new PortfolioItem(cpfCnpj, titulo, descricao, imagemUrl, new Date(System.currentTimeMillis()));
		boolean salvo = portfolioDAO.cadastrar(novoItem);

		if (salvo) {
			enviarRespostaJson(response, HttpServletResponse.SC_CREATED, true, "Item adicionado ao portfólio com sucesso!");
		} else {
			enviarRespostaJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "Erro ao salvar item no portfólio.");
		}
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
			String padrao = "\"" + chave + "\"\\s*:\\s*\"?([^\"\\,\\}]*)\"?";
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
