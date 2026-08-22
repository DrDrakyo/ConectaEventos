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
import br.com.conectaeventos.dao.ContratanteDAO;
import br.com.conectaeventos.dao.ItemContratacaoDAO;
import br.com.conectaeventos.dao.PrestadorDAO;
import br.com.conectaeventos.model.Avaliacao;
import br.com.conectaeventos.model.Contratacao;
import br.com.conectaeventos.model.Contratante;
import br.com.conectaeventos.model.ItemContratacao;
import br.com.conectaeventos.model.Prestador;
import br.com.conectaeventos.utils.SessaoUtils;
import br.com.conectaeventos.utils.ValidadorUtils;

/**
 * Controller responsável pelo acompanhamento, consulta detalhada e atualização de status de contratações.
 * Permite visualizar detalhes da contratação (itens, participantes, avaliação) e alterar status (CONFIRMADO, CONCLUIDO, CANCELADO).
 */
@WebServlet(name = "AcompanharContratacaoController", urlPatterns = { "/acompanharContratacao", "/AcompanharContratacaoController" })
public class AcompanharContratacaoController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ContratacaoDAO contratacaoDAO;
	private ItemContratacaoDAO itemContratacaoDAO;
	private ContratanteDAO contratanteDAO;
	private PrestadorDAO prestadorDAO;
	private AvaliacaoDAO avaliacaoDAO;

	@Override
	public void init() throws ServletException {
		super.init();
		this.contratacaoDAO = new ContratacaoDAO();
		this.itemContratacaoDAO = new ItemContratacaoDAO();
		this.contratanteDAO = new ContratanteDAO();
		this.prestadorDAO = new PrestadorDAO();
		this.avaliacaoDAO = new AvaliacaoDAO();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String idParam = obterParametro(request, "id", "id_contratacao");

		if (!ValidadorUtils.isVazio(idParam)) {
			try {
				int idContratacao = Integer.parseInt(idParam);
				exibirDetalhesContratacao(response, idContratacao);
				return;
			} catch (NumberFormatException e) {
				enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "ID de contratação inválido.");
				return;
			}
		}

		// Se não passou ID, lista contratações do usuário autenticado na sessão
		listarContratacoesDoUsuario(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String idParam = obterParametro(request, "id", "id_contratacao");
		String acao = obterParametro(request, "acao", "action");
		String novoStatus = obterParametro(request, "status", "novo_status");

		String contentType = request.getContentType();
		if (contentType != null && contentType.toLowerCase().contains("application/json") && ValidadorUtils.isVazio(idParam)) {
			String jsonBody = lerCorpoRequisicao(request);
			idParam = extrairCampoJson(jsonBody, "id", "id_contratacao");
			acao = extrairCampoJson(jsonBody, "acao", "action");
			novoStatus = extrairCampoJson(jsonBody, "status", "novo_status");
		}

		if (contratacaoDAO == null) contratacaoDAO = new ContratacaoDAO();

		// Ação: Cadastrar nova contratação
		if ("cadastrar".equalsIgnoreCase(acao) || "criar".equalsIgnoreCase(acao)) {
			String cpfCnpjContratante = obterParametro(request, "cpf_cnpj_contratante");
			String cpfCnpjPrestador = obterParametro(request, "cpf_cnpj_prestador");
			String titulo = obterParametro(request, "titulo_evento");
			String descricao = obterParametro(request, "descricao_evento");
			String dataEventoStr = obterParametro(request, "data_evento");
			
			if (contentType != null && contentType.toLowerCase().contains("application/json") && ValidadorUtils.isVazio(titulo)) {
				String jsonBody = lerCorpoRequisicao(request);
				cpfCnpjContratante = extrairCampoJson(jsonBody, "cpf_cnpj_contratante");
				cpfCnpjPrestador = extrairCampoJson(jsonBody, "cpf_cnpj_prestador");
				titulo = extrairCampoJson(jsonBody, "titulo_evento");
				descricao = extrairCampoJson(jsonBody, "descricao_evento");
				dataEventoStr = extrairCampoJson(jsonBody, "data_evento");
			}

			if (ValidadorUtils.isVazio(cpfCnpjContratante) || ValidadorUtils.isVazio(cpfCnpjPrestador) || ValidadorUtils.isVazio(dataEventoStr)) {
				enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "Dados insuficientes para criar contratação. Informe contratante, prestador e data do evento.");
				return;
			}

			java.sql.Date dataEvento;
			try {
				dataEvento = java.sql.Date.valueOf(dataEventoStr); // Formato esperado: YYYY-MM-DD
			} catch (IllegalArgumentException e) {
				enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "Formato de data inválido. Utilize o formato YYYY-MM-DD.");
				return;
			}

			Contratacao nova = new Contratacao();
			nova.setCpf_cnpj_contratante(cpfCnpjContratante);
			nova.setCpf_cnpj_prestador(cpfCnpjPrestador);
			nova.setTitulo_evento(titulo);
			nova.setDescricao_evento(descricao);
			nova.setData_evento(dataEvento);
			nova.setData_contratacao(new java.sql.Date(System.currentTimeMillis()));
			nova.setStatus("PENDENTE");

			int idGerado = contratacaoDAO.cadastrarComRetornoId(nova);
			if (idGerado > 0) {
				String json = String.format("{\"sucesso\": true, \"mensagem\": \"Contratação criada com sucesso!\", \"id_contratacao\": %d}", idGerado);
				enviarJsonDireto(response, HttpServletResponse.SC_CREATED, json);
			} else {
				enviarRespostaJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false, "Erro ao criar contratação.");
			}
			return;
		}

		if (ValidadorUtils.isVazio(idParam)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "O ID da contratação é obrigatório.");
			return;
		}

		int idContratacao;
		try {
			idContratacao = Integer.parseInt(idParam);
		} catch (NumberFormatException e) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false, "ID da contratação inválido.");
			return;
		}

		Contratacao contratacao = contratacaoDAO.buscarPorId(idContratacao);
		if (contratacao == null) {
			enviarRespostaJson(response, HttpServletResponse.SC_NOT_FOUND, false, "Contratação não encontrada.");
			return;
		}

		// Mapeia ações comuns para o status correspondente
		if ("confirmar".equalsIgnoreCase(acao) || "aprovar".equalsIgnoreCase(acao)) {
			novoStatus = "CONFIRMADO";
		} else if ("concluir".equalsIgnoreCase(acao) || "finalizar".equalsIgnoreCase(acao)) {
			novoStatus = "CONCLUIDO";
		} else if ("cancelar".equalsIgnoreCase(acao)) {
			novoStatus = "CANCELADO";
		}

		if (ValidadorUtils.isVazio(novoStatus)) {
			enviarRespostaJson(response, HttpServletResponse.SC_BAD_REQUEST, false,
					"Informe a ação (confirmar/concluir/cancelar) ou o novo status.");
			return;
		}

		novoStatus = novoStatus.toUpperCase();

		boolean atualizado = contratacaoDAO.atualizarStatus(idContratacao, novoStatus);

		if (atualizado) {
			enviarRespostaJson(response, HttpServletResponse.SC_OK, true,
					"Status da contratação atualizado para '" + novoStatus + "' com sucesso!");
		} else {
			enviarRespostaJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, false,
					"Erro ao atualizar status da contratação.");
		}
	}

	private void exibirDetalhesContratacao(HttpServletResponse response, int idContratacao) throws IOException {
		if (contratacaoDAO == null) contratacaoDAO = new ContratacaoDAO();
		if (itemContratacaoDAO == null) itemContratacaoDAO = new ItemContratacaoDAO();
		if (contratanteDAO == null) contratanteDAO = new ContratanteDAO();
		if (prestadorDAO == null) prestadorDAO = new PrestadorDAO();
		if (avaliacaoDAO == null) avaliacaoDAO = new AvaliacaoDAO();

		Contratacao c = contratacaoDAO.buscarPorId(idContratacao);
		if (c == null) {
			enviarRespostaJson(response, HttpServletResponse.SC_NOT_FOUND, false, "Contratação não encontrada.");
			return;
		}

		Contratante contratante = contratanteDAO.buscarPorCpfCnpj(c.getCpf_cnpj_contratante());
		Prestador prestador = prestadorDAO.buscarPorCpfCnpj(c.getCpf_cnpj_prestador());
		List<ItemContratacao> itens = itemContratacaoDAO.listarPorContratacao(idContratacao);
		Avaliacao avaliacao = avaliacaoDAO.buscarPorContratacao(idContratacao);

		StringBuilder json = new StringBuilder();
		json.append("{")
			.append("\"sucesso\": true, ")
			.append("\"contratacao\": {")
			.append("\"id_contratacao\": ").append(c.getId_contratacao()).append(", ")
			.append("\"titulo_evento\": \"").append(escaparJson(c.getTitulo_evento())).append("\", ")
			.append("\"descricao_evento\": \"").append(escaparJson(c.getDescricao_evento())).append("\", ")
			.append("\"data_evento\": \"").append(c.getData_evento() != null ? c.getData_evento().toString() : "").append("\", ")
			.append("\"data_contratacao\": \"").append(c.getData_contratacao() != null ? c.getData_contratacao().toString() : "").append("\", ")
			.append("\"valor_total\": ").append(String.format(java.util.Locale.US, "%.2f", c.getValor_total())).append(", ")
			.append("\"status\": \"").append(escaparJson(c.getStatus())).append("\"")
			.append("}, ");

		// Contratante
		json.append("\"contratante\": {")
			.append("\"cpf_cnpj\": \"").append(escaparJson(c.getCpf_cnpj_contratante())).append("\", ")
			.append("\"nome\": \"").append(contratante != null ? escaparJson(contratante.getNome_contratante()) : "").append("\", ")
			.append("\"telefone\": \"").append(contratante != null ? escaparJson(contratante.getTelefone()) : "").append("\", ")
			.append("\"email\": \"").append(contratante != null ? escaparJson(contratante.getEmail_contratante()) : "").append("\"")
			.append("}, ");

		// Prestador
		json.append("\"prestador\": {")
			.append("\"cpf_cnpj\": \"").append(escaparJson(c.getCpf_cnpj_prestador())).append("\", ")
			.append("\"nome\": \"").append(prestador != null ? escaparJson(prestador.getNome_prestador()) : "").append("\", ")
			.append("\"categoria\": \"").append(prestador != null ? escaparJson(prestador.getCategoria()) : "").append("\", ")
			.append("\"telefone\": \"").append(prestador != null ? escaparJson(prestador.getTelefone()) : "").append("\", ")
			.append("\"email\": \"").append(prestador != null ? escaparJson(prestador.getEmail_prestador()) : "").append("\"")
			.append("}, ");

		// Itens
		json.append("\"itens\": [");
		for (int i = 0; i < itens.size(); i++) {
			ItemContratacao item = itens.get(i);
			json.append("{")
				.append("\"id_item\": ").append(item.getId_item()).append(", ")
				.append("\"descricao_item\": \"").append(escaparJson(item.getDescricao_item())).append("\", ")
				.append("\"quantidade\": ").append(item.getQuantidade()).append(", ")
				.append("\"valor_unitario\": ").append(String.format(java.util.Locale.US, "%.2f", item.getValor_unitario())).append(", ")
				.append("\"valor_total\": ").append(String.format(java.util.Locale.US, "%.2f", item.getValor_total()))
				.append("}");
			if (i < itens.size() - 1) {
				json.append(", ");
			}
		}
		json.append("], ");

		// Avaliação vinculada
		if (avaliacao != null) {
			json.append("\"avaliacao\": {")
				.append("\"id_avaliacao\": ").append(avaliacao.getId_avaliacao()).append(", ")
				.append("\"nota\": ").append(avaliacao.getNota()).append(", ")
				.append("\"comentario\": \"").append(escaparJson(avaliacao.getComentario())).append("\", ")
				.append("\"data_avaliacao\": \"").append(avaliacao.getData_avaliacao() != null ? avaliacao.getData_avaliacao().toString() : "").append("\"")
				.append("}");
		} else {
			json.append("\"avaliacao\": null");
		}

		json.append("}");

		enviarJsonDireto(response, HttpServletResponse.SC_OK, json.toString());
	}

	private void listarContratacoesDoUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (contratacaoDAO == null) contratacaoDAO = new ContratacaoDAO();

		Object objContratante = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_CONTRATANTE);
		Object objPrestador = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_PRESTADOR);

		List<Contratacao> lista;
		if (objContratante instanceof Contratante) {
			lista = contratacaoDAO.listarPorContratante(((Contratante) objContratante).getCpf_cnpj());
		} else if (objPrestador instanceof Prestador) {
			lista = contratacaoDAO.listarPorPrestador(((Prestador) objPrestador).getCpf_cnpj());
		} else {
			enviarRespostaJson(response, HttpServletResponse.SC_UNAUTHORIZED, false,
					"Acesso não autorizado. Faça login ou informe o ID da contratação.");
			return;
		}

		StringBuilder json = new StringBuilder();
		json.append("{\"sucesso\": true, \"total\": ").append(lista.size()).append(", \"contratacoes\": [");

		for (int i = 0; i < lista.size(); i++) {
			Contratacao c = lista.get(i);
			json.append("{")
				.append("\"id_contratacao\": ").append(c.getId_contratacao()).append(", ")
				.append("\"cpf_cnpj_contratante\": \"").append(escaparJson(c.getCpf_cnpj_contratante())).append("\", ")
				.append("\"cpf_cnpj_prestador\": \"").append(escaparJson(c.getCpf_cnpj_prestador())).append("\", ")
				.append("\"titulo_evento\": \"").append(escaparJson(c.getTitulo_evento())).append("\", ")
				.append("\"data_evento\": \"").append(c.getData_evento() != null ? c.getData_evento().toString() : "").append("\", ")
				.append("\"data_contratacao\": \"").append(c.getData_contratacao() != null ? c.getData_contratacao().toString() : "").append("\", ")
				.append("\"valor_total\": ").append(String.format(java.util.Locale.US, "%.2f", c.getValor_total())).append(", ")
				.append("\"status\": \"").append(escaparJson(c.getStatus())).append("\"")
				.append("}");
			if (i < lista.size() - 1) {
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
