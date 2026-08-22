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

import br.com.conectaeventos.dao.AvaliacaoDAO;
import br.com.conectaeventos.dao.ContratacaoDAO;
import br.com.conectaeventos.dao.PrestadorDAO;
import br.com.conectaeventos.model.Avaliacao;
import br.com.conectaeventos.model.Contratacao;
import br.com.conectaeventos.model.Contratante;
import br.com.conectaeventos.model.Prestador;
import br.com.conectaeventos.utils.SessaoUtils;
import br.com.conectaeventos.utils.ValidadorUtils;

/**
 * Controller responsável pela avaliação de prestadores de serviços.
 * Permite listar avaliações recebidas por um prestador (GET) e registrar uma nova avaliação
 * vinculada a uma contratação concluída (POST).
 */
@WebServlet(name = "AvaliarPrestadorController", urlPatterns = { "/avaliarPrestador", "/AvaliarPrestadorController" })
public class AvaliarPrestadorController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private AvaliacaoDAO avaliacaoDAO;
	private ContratacaoDAO contratacaoDAO;
	private PrestadorDAO prestadorDAO;

	@Override
	public void init() throws ServletException {
		super.init();
		this.avaliacaoDAO = new AvaliacaoDAO();
		this.contratacaoDAO = new ContratacaoDAO();
		this.prestadorDAO = new PrestadorDAO();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String idContratacaoParam = obterParametro(request, "id_contratacao", "contratacao");
		String cpfCnpjPrestador = obterParametro(request, "cpf_cnpj_prestador", "cpf_cnpj", "prestador");

		if (avaliacaoDAO == null) avaliacaoDAO = new AvaliacaoDAO();

		// Se forneceu ID de contratação, consulta se já foi avaliada
		if (!ValidadorUtils.isVazio(idContratacaoParam)) {
			try {
				int idContratacao = Integer.parseInt(idContratacaoParam);
				Avaliacao a = avaliacaoDAO.buscarPorContratacao(idContratacao);
				if (a != null) {
					String json = String.format(
							"{\"sucesso\": true, \"avaliada\": true, \"avaliacao\": {\"id_avaliacao\": %d, \"id_contratacao\": %d, \"nota\": %d, \"comentario\": \"%s\", \"data_avaliacao\": \"%s\"}}",
							a.getId_avaliacao(), a.getId_contratacao(), a.getNota(), escaparJson(a.getComentario()),
							a.getData_avaliacao() != null ? a.getData_avaliacao().toString() : "");
					enviarJsonDireto(response, HttpServletResponse.SC_OK, json);
				} else {
					enviarJsonDireto(response, HttpServletResponse.SC_OK,
							"{\"sucesso\": true, \"avaliada\": false, \"mensagem\": \"Esta contratação ainda não foi avaliada.\"}");
				}
				return;
			} catch (NumberFormatException e) {
				enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "ID de contratação inválido.");
				return;
			}
		}

		// Se forneceu CPF/CNPJ de prestador, lista suas avaliações
		if (!ValidadorUtils.isVazio(cpfCnpjPrestador)) {
			List<Avaliacao> lista = avaliacaoDAO.listarPorPrestador(cpfCnpjPrestador);
			double media = avaliacaoDAO.calcularMediaPrestador(cpfCnpjPrestador);
			int total = avaliacaoDAO.contarAvaliacoesPrestador(cpfCnpjPrestador);

			StringBuilder json = new StringBuilder();
			json.append("{")
				.append("\"sucesso\": true, ")
				.append("\"cpf_cnpj_prestador\": \"").append(escaparJson(cpfCnpjPrestador)).append("\", ")
				.append("\"media_avaliacoes\": ").append(String.format(java.util.Locale.US, "%.1f", media)).append(", ")
				.append("\"total_avaliacoes\": ").append(total).append(", ")
				.append("\"avaliacoes\": [");

			for (int i = 0; i < lista.size(); i++) {
				Avaliacao a = lista.get(i);
				json.append("{")
					.append("\"id_avaliacao\": ").append(a.getId_avaliacao()).append(", ")
					.append("\"id_contratacao\": ").append(a.getId_contratacao()).append(", ")
					.append("\"cpf_cnpj_contratante\": \"").append(escaparJson(a.getCpf_cnpj_contratante())).append("\", ")
					.append("\"nota\": ").append(a.getNota()).append(", ")
					.append("\"comentario\": \"").append(escaparJson(a.getComentario())).append("\", ")
					.append("\"data_avaliacao\": \"").append(a.getData_avaliacao() != null ? a.getData_avaliacao().toString() : "").append("\"")
					.append("}");
				if (i < lista.size() - 1) {
					json.append(", ");
				}
			}

			json.append("]}");
			enviarJsonDireto(response, HttpServletResponse.SC_OK, json.toString());
			return;
		}

		enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false,
				"Informe 'id_contratacao' ou 'cpf_cnpj_prestador'.");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String idContratacaoParam = obterParametro(request, "id_contratacao", "contratacao");
		String notaParam = obterParametro(request, "nota");
		String comentario = obterParametro(request, "comentario", "feedback");

		String contentType = request.getContentType();
		if (contentType != null && contentType.toLowerCase().contains("application/json") && ValidadorUtils.isVazio(idContratacaoParam)) {
			String jsonBody = lerCorpoRequisicao(request);
			idContratacaoParam = extrairCampoJson(jsonBody, "id_contratacao", "contratacao");
			notaParam = extrairCampoJson(jsonBody, "nota");
			comentario = extrairCampoJson(jsonBody, "comentario", "feedback");
		}

		if (ValidadorUtils.isVazio(idContratacaoParam) || ValidadorUtils.isVazio(notaParam)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false,
					"Os campos 'id_contratacao' e 'nota' são obrigatórios.");
			return;
		}

		int idContratacao;
		int nota;
		try {
			idContratacao = Integer.parseInt(idContratacaoParam);
			nota = Integer.parseInt(notaParam);
		} catch (NumberFormatException e) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false,
					"Valores numéricos inválidos para 'id_contratacao' ou 'nota'.");
			return;
		}

		if (nota < 1 || nota > 5) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false,
					"A nota deve ser um valor inteiro entre 1 e 5.");
			return;
		}

		if (contratacaoDAO == null) contratacaoDAO = new ContratacaoDAO();
		if (avaliacaoDAO == null) avaliacaoDAO = new AvaliacaoDAO();

		Contratacao contratacao = contratacaoDAO.buscarPorId(idContratacao);
		if (contratacao == null) {
			enviarRespostaJson(response, HttpServletResponse.SC_NOT_FOUND, false, "Contratação não encontrada.");
			return;
		}

		// Valida se a contratação já foi concluída
		if (!"CONCLUIDO".equalsIgnoreCase(contratacao.getStatus())) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false,
					"Apenas contratações com status 'CONCLUIDO' podem ser avaliadas.");
			return;
		}

		// Verifica se já existe avaliação para esta contratação
		Avaliacao avaliacaoExistente = avaliacaoDAO.buscarPorContratacao(idContratacao);
		if (avaliacaoExistente != null) {
			enviarRespostaJson(response, HttpServletResponse.SC_CONFLICT, false,
					"Esta contratação já foi avaliada anteriormente.");
			return;
		}

		// Identifica o autor (Contratante)
		String cpfCnpjContratante = contratacao.getCpf_cnpj_contratante();
		String cpfCnpjPrestador = contratacao.getCpf_cnpj_prestador();

		Avaliacao novaAvaliacao = new Avaliacao(
				idContratacao,
				cpfCnpjContratante,
				cpfCnpjPrestador,
				nota,
				comentario,
				new Date(System.currentTimeMillis())
		);

		boolean salva = avaliacaoDAO.cadastrar(novaAvaliacao);

		if (salva) {
			enviarRespostaJson(response, HttpServletResponse.SC_CREATED, true,
					"Avaliação registrada com sucesso! Obrigado pelo seu feedback.");
		} else {
			enviarRespostaJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false,
					"Erro ao registrar a avaliação no banco de dados.");
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
