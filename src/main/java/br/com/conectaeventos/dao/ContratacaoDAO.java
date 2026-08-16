package br.com.conectaeventos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.conectaeventos.model.Contratacao;
import connection.ConnectionFactory;

public class ContratacaoDAO {

	/**
	 * Cadastra uma nova contratação no banco de dados.
	 * 
	 * @param contratacao Objeto Contratacao a ser salvo.
	 * @return boolean true se inserido com sucesso, false caso contrário.
	 */
	public boolean cadastrar(Contratacao contratacao) {
		String sql = "INSERT INTO contratacao (cpf_cnpj_contratante, cpf_cnpj_prestador, titulo_evento, "
				+ "descricao_evento, data_evento, data_contratacao, valor_total, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);

			stmt.setString(1, contratacao.getCpf_cnpj_contratante());
			stmt.setString(2, contratacao.getCpf_cnpj_prestador());
			stmt.setString(3, contratacao.getTitulo_evento());
			stmt.setString(4, contratacao.getDescricao_evento());
			stmt.setDate(5, contratacao.getData_evento());
			stmt.setDate(6, contratacao.getData_contratacao());
			stmt.setDouble(7, contratacao.getValor_total());
			stmt.setString(8, contratacao.getStatus());

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("Erro ao cadastrar contratação: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Cadastra uma contratação e retorna o ID auto-gerado pelo banco.
	 * 
	 * @param contratacao Objeto Contratacao a ser salvo.
	 * @return int ID gerado ou -1 em caso de falha.
	 */
	public int cadastrarComRetornoId(Contratacao contratacao) {
		String sql = "INSERT INTO contratacao (cpf_cnpj_contratante, cpf_cnpj_prestador, titulo_evento, "
				+ "descricao_evento, data_evento, data_contratacao, valor_total, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, contratacao.getCpf_cnpj_contratante());
			stmt.setString(2, contratacao.getCpf_cnpj_prestador());
			stmt.setString(3, contratacao.getTitulo_evento());
			stmt.setString(4, contratacao.getDescricao_evento());
			stmt.setDate(5, contratacao.getData_evento());
			stmt.setDate(6, contratacao.getData_contratacao());
			stmt.setDouble(7, contratacao.getValor_total());
			stmt.setString(8, contratacao.getStatus());

			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();

			if (rs.next()) {
				int idGerado = rs.getInt(1);
				contratacao.setId_contratacao(idGerado);
				return idGerado;
			}
		} catch (SQLException e) {
			System.err.println("Erro ao cadastrar contratação com retorno de ID: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return -1;
	}

	/**
	 * Busca uma contratação pelo seu ID.
	 * 
	 * @param idContratacao ID da contratação.
	 * @return Contratacao encontrada ou null se não existir.
	 */
	public Contratacao buscarPorId(int idContratacao) {
		String sql = "SELECT * FROM contratacao WHERE id_contratacao = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Contratacao contratacao = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idContratacao);
			rs = stmt.executeQuery();

			if (rs.next()) {
				contratacao = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar contratação por ID: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return contratacao;
	}

	/**
	 * Lista todas as contratações realizadas por um contratante específico.
	 * 
	 * @param cpfCnpjContratante CPF ou CNPJ do contratante.
	 * @return List<Contratacao> lista de contratações.
	 */
	public List<Contratacao> listarPorContratante(String cpfCnpjContratante) {
		String sql = "SELECT * FROM contratacao WHERE cpf_cnpj_contratante = ? ORDER BY data_contratacao DESC";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Contratacao> lista = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpjContratante);
			rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(mapearResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar contratações por contratante: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return lista;
	}

	/**
	 * Lista todas as contratações recebidas por um prestador específico.
	 * 
	 * @param cpfCnpjPrestador CPF ou CNPJ do prestador.
	 * @return List<Contratacao> lista de contratações.
	 */
	public List<Contratacao> listarPorPrestador(String cpfCnpjPrestador) {
		String sql = "SELECT * FROM contratacao WHERE cpf_cnpj_prestador = ? ORDER BY data_contratacao DESC";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Contratacao> lista = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpjPrestador);
			rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(mapearResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar contratações por prestador: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return lista;
	}

	/**
	 * Lista todas as contratações cadastradas no sistema.
	 * 
	 * @return List<Contratacao> lista completa.
	 */
	public List<Contratacao> listarTodos() {
		String sql = "SELECT * FROM contratacao ORDER BY data_contratacao DESC";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Contratacao> lista = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(mapearResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar todas as contratações: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return lista;
	}

	/**
	 * Conta a quantidade de contratações do contratante filtradas por status.
	 * 
	 * @param cpfCnpjContratante CPF ou CNPJ do contratante.
	 * @param status Status a filtrar (ou null/vazio para total).
	 * @return int contagem.
	 */
	public int contarPorContratanteEStatus(String cpfCnpjContratante, String status) {
		String sql = "SELECT COUNT(*) FROM contratacao WHERE cpf_cnpj_contratante = ?"
				+ (status != null && !status.isEmpty() ? " AND status = ?" : "");
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpjContratante);
			if (status != null && !status.isEmpty()) {
				stmt.setString(2, status);
			}
			rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao contar contratações por contratante: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return 0;
	}

	/**
	 * Conta a quantidade de contratações do prestador filtradas por status.
	 * 
	 * @param cpfCnpjPrestador CPF ou CNPJ do prestador.
	 * @param status Status a filtrar (ou null/vazio para total).
	 * @return int contagem.
	 */
	public int contarPorPrestadorEStatus(String cpfCnpjPrestador, String status) {
		String sql = "SELECT COUNT(*) FROM contratacao WHERE cpf_cnpj_prestador = ?"
				+ (status != null && !status.isEmpty() ? " AND status = ?" : "");
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpjPrestador);
			if (status != null && !status.isEmpty()) {
				stmt.setString(2, status);
			}
			rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao contar contratações por prestador: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return 0;
	}

	/**
	 * Atualiza os dados de uma contratação existente.
	 * 
	 * @param contratacao Objeto Contratacao com dados atualizados.
	 * @return boolean true se atualizado com sucesso, false caso contrário.
	 */
	public boolean atualizar(Contratacao contratacao) {
		String sql = "UPDATE contratacao SET cpf_cnpj_contratante = ?, cpf_cnpj_prestador = ?, "
				+ "titulo_evento = ?, descricao_evento = ?, data_evento = ?, data_contratacao = ?, "
				+ "valor_total = ?, status = ? WHERE id_contratacao = ?";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);

			stmt.setString(1, contratacao.getCpf_cnpj_contratante());
			stmt.setString(2, contratacao.getCpf_cnpj_prestador());
			stmt.setString(3, contratacao.getTitulo_evento());
			stmt.setString(4, contratacao.getDescricao_evento());
			stmt.setDate(5, contratacao.getData_evento());
			stmt.setDate(6, contratacao.getData_contratacao());
			stmt.setDouble(7, contratacao.getValor_total());
			stmt.setString(8, contratacao.getStatus());
			stmt.setInt(9, contratacao.getId_contratacao());

			int linhasAfetadas = stmt.executeUpdate();
			return linhasAfetadas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao atualizar contratação: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Atualiza apenas o status de uma contratação.
	 * 
	 * @param idContratacao ID da contratação.
	 * @param novoStatus Novo status (ex: PENDENTE, CONCLUIDO, CANCELADO, EM_ANDAMENTO).
	 * @return boolean true se atualizado com sucesso, false caso contrário.
	 */
	public boolean atualizarStatus(int idContratacao, String novoStatus) {
		String sql = "UPDATE contratacao SET status = ? WHERE id_contratacao = ?";
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, novoStatus);
			stmt.setInt(2, idContratacao);

			int linhasAfetadas = stmt.executeUpdate();
			return linhasAfetadas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao atualizar status da contratação " + idContratacao + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Exclui uma contratação pelo seu ID.
	 * 
	 * @param idContratacao ID da contratação.
	 * @return boolean true se excluída com sucesso, false caso contrário.
	 */
	public boolean deletar(int idContratacao) {
		String sql = "DELETE FROM contratacao WHERE id_contratacao = ?";
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idContratacao);

			int linhasAfetadas = stmt.executeUpdate();
			return linhasAfetadas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao deletar contratação " + idContratacao + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Mapeia um registro ResultSet para o objeto Contratacao.
	 */
	private Contratacao mapearResultSet(ResultSet rs) throws SQLException {
		Contratacao c = new Contratacao();
		c.setId_contratacao(rs.getInt("id_contratacao"));
		c.setCpf_cnpj_contratante(rs.getString("cpf_cnpj_contratante"));
		c.setCpf_cnpj_prestador(rs.getString("cpf_cnpj_prestador"));
		c.setTitulo_evento(rs.getString("titulo_evento"));
		c.setDescricao_evento(rs.getString("descricao_evento"));
		c.setData_evento(rs.getDate("data_evento"));
		c.setData_contratacao(rs.getDate("data_contratacao"));
		c.setValor_total(rs.getDouble("valor_total"));
		c.setStatus(rs.getString("status"));
		return c;
	}
}
