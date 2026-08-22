package br.com.conectaeventos.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.conectaeventos.dao.ContratacaoDAO;
import br.com.conectaeventos.dao.ContratanteDAO;
import br.com.conectaeventos.model.Contratacao;
import br.com.conectaeventos.model.Contratante;
import br.com.conectaeventos.utils.SessaoUtils;
import br.com.conectaeventos.utils.ValidadorUtils;

/**
 * Controller responsável pela exibição detalhada do perfil do Contratante autenticado,
 * incluindo seus dados cadastrais, resumo de contratações por status e lista de eventos contratados.
 */
@WebServlet(name = "MeuPerfilContratanteController", urlPatterns = { "/meuPerfilContratante", "/MeuPerfilContratanteController" })
public class MeuPerfilContratanteController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ContratanteDAO contratanteDAO;
	private ContratacaoDAO contratacaoDAO;

	@Override
	public void init() throws ServletException {
		super.init();
		this.contratanteDAO = new ContratanteDAO();
		this.contratacaoDAO = new ContratacaoDAO();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		Contratante contratante = obterContratanteDaRequisicao(request);

		if (contratante == null) {
			enviarRespostaJson(response, HttpServletResponse.SC_UNAUTHORIZED, false,
					"Acesso não autorizado. Nenhum contratante autenticado.");
			return;
		}

		if (contratacaoDAO == null) {
			contratacaoDAO = new ContratacaoDAO();
		}

		String cpfCnpj = contratante.getCpf_cnpj();
		int totalContratacoes = contratacaoDAO.contarPorContratanteEStatus(cpfCnpj, null);
		int totalPendentes = contratacaoDAO.contarPorContratanteEStatus(cpfCnpj, "PENDENTE");
		int totalConfirmadas = contratacaoDAO.contarPorContratanteEStatus(cpfCnpj, "CONFIRMADO");
		int totalConcluidas = contratacaoDAO.contarPorContratanteEStatus(cpfCnpj, "CONCLUIDO");
		int totalCanceladas = contratacaoDAO.contarPorContratanteEStatus(cpfCnpj, "CANCELADO");

		List<Contratacao> listaContratacoes = contratacaoDAO.listarPorContratante(cpfCnpj);

		StringBuilder json = new StringBuilder();
		json.append("{")
			.append("\"sucesso\": true, ")
			.append("\"perfil\": {")
			.append("\"id_contratante\": ").append(contratante.getId_contratante()).append(", ")
			.append("\"nome_contratante\": \"").append(escaparJson(contratante.getNome_contratante())).append("\", ")
			.append("\"email_contratante\": \"").append(escaparJson(contratante.getEmail_contratante())).append("\", ")
			.append("\"cpf_cnpj\": \"").append(escaparJson(contratante.getCpf_cnpj())).append("\", ")
			.append("\"telefone\": \"").append(escaparJson(contratante.getTelefone())).append("\", ")
			.append("\"endereco\": \"").append(escaparJson(contratante.getEndereco())).append("\", ")
			.append("\"cidade\": \"").append(escaparJson(contratante.getCidade())).append("\", ")
			.append("\"situacao\": \"").append(escaparJson(contratante.getSituacao())).append("\", ")
			.append("\"data_cadastro\": \"").append(contratante.getData_cadastro() != null ? contratante.getData_cadastro().toString() : "").append("\"")
			.append("}, ")
			.append("\"estatisticas\": {")
			.append("\"total_contratacoes\": ").append(totalContratacoes).append(", ")
			.append("\"pendentes\": ").append(totalPendentes).append(", ")
			.append("\"confirmadas\": ").append(totalConfirmadas).append(", ")
			.append("\"concluidas\": ").append(totalConcluidas).append(", ")
			.append("\"canceladas\": ").append(totalCanceladas)
			.append("}, ")
			.append("\"contratacoes\": [");

		for (int i = 0; i < listaContratacoes.size(); i++) {
			Contratacao c = listaContratacoes.get(i);
			json.append("{")
				.append("\"id_contratacao\": ").append(c.getId_contratacao()).append(", ")
				.append("\"cpf_cnpj_prestador\": \"").append(escaparJson(c.getCpf_cnpj_prestador())).append("\", ")
				.append("\"titulo_evento\": \"").append(escaparJson(c.getTitulo_evento())).append("\", ")
				.append("\"descricao_evento\": \"").append(escaparJson(c.getDescricao_evento())).append("\", ")
				.append("\"data_evento\": \"").append(c.getData_evento() != null ? c.getData_evento().toString() : "").append("\", ")
				.append("\"data_contratacao\": \"").append(c.getData_contratacao() != null ? c.getData_contratacao().toString() : "").append("\", ")
				.append("\"valor_total\": ").append(String.format(java.util.Locale.US, "%.2f", c.getValor_total())).append(", ")
				.append("\"status\": \"").append(escaparJson(c.getStatus())).append("\"")
				.append("}");
			if (i < listaContratacoes.size() - 1) {
				json.append(", ");
			}
		}

		json.append("]}");

		enviarJsonDireto(response, HttpServletResponse.SC_OK, json.toString());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private Contratante obterContratanteDaRequisicao(HttpServletRequest request) {
		Object objSessao = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_CONTRATANTE);
		if (objSessao == null) {
			objSessao = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_USUARIO);
		}

		if (objSessao instanceof Contratante) {
			return (Contratante) objSessao;
		}

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

	private String obterParametro(HttpServletRequest request, String... nomes) {
		for (String nome : nomes) {
			String valor = request.getParameter(nome);
			if (valor != null && !valor.trim().isEmpty()) {
				return valor.trim();
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
