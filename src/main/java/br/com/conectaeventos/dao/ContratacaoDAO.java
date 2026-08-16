package br.com.conectaeventos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.conectaeventos.model.Contratacao;
import connection.ConnectionFactory;

public class ContratacaoDAO {

	/**
	 * Cadastra uma nova contratação no banco de dados.
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
	 * Lista todas as contratações realizadas por um contratante específico.
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
	 * Conta a quantidade de contratações do contratante filtradas por status.
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
			System.err.println("Erro ao contar contratações: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return 0;
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

