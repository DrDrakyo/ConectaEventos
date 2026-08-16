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
 * Controller responsável por fornecer os dados e métricas do Dashboard (painel) do Contratante.
 * Retorna estatísticas de contratações, perfil e lista de eventos contratados em formato JSON.
 */
@WebServlet(name = "DashboardContratanteController", urlPatterns = { "/dashboardContratante", "/DashboardContratanteController" })
public class DashboardContratanteController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ContratanteDAO contratanteDAO;
	private ContratacaoDAO contratacaoDAO;

	@Override
	public void init() throws ServletException {
		super.init();
		this.contratanteDAO = new ContratanteDAO();
		this.contratacaoDAO = new ContratacaoDAO();
	}

	/**
	 * Retorna os dados resumidos do Dashboard do Contratante em formato JSON.
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

		if (contratacaoDAO == null) {
			contratacaoDAO = new ContratacaoDAO();
		}

		String cpfCnpj = contratante.getCpf_cnpj();

		// Busca métricas de contratações
		int totalContratacoes = contratacaoDAO.contarPorContratanteEStatus(cpfCnpj, null);
		int pendentes = contratacaoDAO.contarPorContratanteEStatus(cpfCnpj, "PENDENTE");
		int concluidas = contratacaoDAO.contarPorContratanteEStatus(cpfCnpj, "CONCLUIDO");
		int canceladas = contratacaoDAO.contarPorContratanteEStatus(cpfCnpj, "CANCELADO");

		// Lista contratações recentes
		List<Contratacao> ultimasContratacoes = contratacaoDAO.listarPorContratante(cpfCnpj);

		// Monta JSON das contratações recentes
		StringBuilder jsonContratacoes = new StringBuilder("[");
		if (ultimasContratacoes != null && !ultimasContratacoes.isEmpty()) {
			for (int i = 0; i < ultimasContratacoes.size(); i++) {
				Contratacao c = ultimasContratacoes.get(i);
				jsonContratacoes.append(String.format(
						"{\"id_contratacao\": %d, \"titulo_evento\": \"%s\", \"descricao_evento\": \"%s\", "
								+ "\"data_evento\": \"%s\", \"data_contratacao\": \"%s\", \"valor_total\": %.2f, \"status\": \"%s\"}",
						c.getId_contratacao(),
						escaparJson(c.getTitulo_evento()),
						escaparJson(c.getDescricao_evento()),
						c.getData_evento() != null ? c.getData_evento().toString() : "",
						c.getData_contratacao() != null ? c.getData_contratacao().toString() : "",
						c.getValor_total(),
						escaparJson(c.getStatus())
				));

				if (i < ultimasContratacoes.size() - 1) {
					jsonContratacoes.append(",");
				}
			}
		}
		jsonContratacoes.append("]");

		// Monta JSON completo do Dashboard
		String jsonDashboard = String.format(
				"{\"sucesso\": true, \"contratante\": {\"id\": %d, \"nome\": \"%s\", \"email\": \"%s\", \"cidade\": \"%s\", \"situacao\": \"%s\"}, "
						+ "\"resumo\": {\"total_contratacoes\": %d, \"pendentes\": %d, \"concluidas\": %d, \"canceladas\": %d}, "
						+ "\"ultimas_contratacoes\": %s}",
				contratante.getId_contratante(),
				escaparJson(contratante.getNome_contratante()),
				escaparJson(contratante.getEmail_contratante()),
				escaparJson(contratante.getCidade()),
				escaparJson(contratante.getSituacao()),
				totalContratacoes,
				pendentes,
				concluidas,
				canceladas,
				jsonContratacoes.toString()
		);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonDashboard);
		response.getWriter().flush();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Encaminha requisições POST para doGet (consulta de dados do painel)
		doGet(request, response);
	}

	/**
	 * Identifica o contratante a partir da sessão HTTP ou de parâmetros da requisição.
	 */
	private Contratante obterContratanteDaRequisicao(HttpServletRequest request) {
		Object objetoSessao = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_CONTRATANTE);
		if (objetoSessao == null) {
			objetoSessao = SessaoUtils.obterDaSessao(request, SessaoUtils.CHAVE_USUARIO);
		}

		if (objetoSessao instanceof Contratante) {
			return (Contratante) objetoSessao;
		}

		String cpfCnpj = request.getParameter("cpf_cnpj");
		if (ValidadorUtils.isVazio(cpfCnpj)) {
			cpfCnpj = request.getParameter("cpfCnpj");
		}
		String email = request.getParameter("email");
		if (ValidadorUtils.isVazio(email)) {
			email = request.getParameter("email_contratante");
		}

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
	 * Escapa caracteres especiais para formato JSON.
	 */
	private String escaparJson(String texto) {
		if (texto == null) {
			return "";
		}
		return texto.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	/**
	 * Envia resposta JSON de erro ou aviso.
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

