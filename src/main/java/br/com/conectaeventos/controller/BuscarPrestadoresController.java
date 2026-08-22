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
import br.com.conectaeventos.dao.PrestadorDAO;
import br.com.conectaeventos.model.Prestador;

/**
 * Controller responsável pela busca e listagem de prestadores de serviços.
 * Permite filtrar prestadores ativos por termo de busca, categoria e cidade.
 * Retorna dados formatados em JSON incluindo a nota média de avaliações.
 */
@WebServlet(name = "BuscarPrestadoresController", urlPatterns = { "/buscarPrestadores", "/BuscarPrestadoresController" })
public class BuscarPrestadoresController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private PrestadorDAO prestadorDAO;
	private AvaliacaoDAO avaliacaoDAO;

	@Override
	public void init() throws ServletException {
		super.init();
		this.prestadorDAO = new PrestadorDAO();
		this.avaliacaoDAO = new AvaliacaoDAO();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String termo = obterParametro(request, "termo", "q", "busca", "nome");
		String categoria = obterParametro(request, "categoria");
		String cidade = obterParametro(request, "cidade");

		processarBusca(response, termo, categoria, cidade);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String termo = obterParametro(request, "termo", "q", "busca", "nome");
		String categoria = obterParametro(request, "categoria");
		String cidade = obterParametro(request, "cidade");

		String contentType = request.getContentType();
		if (contentType != null && contentType.toLowerCase().contains("application/json") && termo.isEmpty()
				&& categoria.isEmpty() && cidade.isEmpty()) {
			String jsonBody = lerCorpoRequisicao(request);
			termo = extrairCampoJson(jsonBody, "termo", "q", "busca", "nome");
			categoria = extrairCampoJson(jsonBody, "categoria");
			cidade = extrairCampoJson(jsonBody, "cidade");
		}

		processarBusca(response, termo, categoria, cidade);
	}

	private void processarBusca(HttpServletResponse response, String termo, String categoria, String cidade)
			throws IOException {

		if (prestadorDAO == null) {
			prestadorDAO = new PrestadorDAO();
		}
		if (avaliacaoDAO == null) {
			avaliacaoDAO = new AvaliacaoDAO();
		}

		List<Prestador> prestadores;
		if (termo.isEmpty() && categoria.isEmpty() && cidade.isEmpty()) {
			prestadores = prestadorDAO.listarAtivos();
		} else {
			prestadores = prestadorDAO.buscarPorFiltros(termo, categoria, cidade);
		}

		StringBuilder json = new StringBuilder();
		json.append("{\"sucesso\": true, \"total\": ").append(prestadores.size()).append(", \"prestadores\": [");

		for (int i = 0; i < prestadores.size(); i++) {
			Prestador p = prestadores.get(i);
			double media = avaliacaoDAO.calcularMediaPrestador(p.getCpf_cnpj());
			int totalAvaliacoes = avaliacaoDAO.contarAvaliacoesPrestador(p.getCpf_cnpj());

			json.append("{")
				.append("\"id_prestador\": ").append(p.getId_prestador()).append(", ")
				.append("\"nome_prestador\": \"").append(escaparJson(p.getNome_prestador())).append("\", ")
				.append("\"email_prestador\": \"").append(escaparJson(p.getEmail_prestador())).append("\", ")
				.append("\"cpf_cnpj\": \"").append(escaparJson(p.getCpf_cnpj())).append("\", ")
				.append("\"telefone\": \"").append(escaparJson(p.getTelefone())).append("\", ")
				.append("\"endereco\": \"").append(escaparJson(p.getEndereco())).append("\", ")
				.append("\"cidade\": \"").append(escaparJson(p.getCidade())).append("\", ")
				.append("\"categoria\": \"").append(escaparJson(p.getCategoria())).append("\", ")
				.append("\"descricao\": \"").append(escaparJson(p.getDescricao())).append("\", ")
				.append("\"situacao\": \"").append(escaparJson(p.getSituacao())).append("\", ")
				.append("\"media_avaliacoes\": ").append(String.format(java.util.Locale.US, "%.1f", media)).append(", ")
				.append("\"total_avaliacoes\": ").append(totalAvaliacoes)
				.append("}");

			if (i < prestadores.size() - 1) {
				json.append(", ");
			}
		}

		json.append("]}");

		enviarJsonDireto(response, HttpServletResponse.SC_OK, json.toString());
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
			// Ignora erro de leitura
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

	private void enviarJsonDireto(HttpServletResponse response, int status, String json) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		response.getWriter().flush();
	}
}
