package br.com.conectaeventos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.conectaeventos.model.Avaliacao;
import connection.ConnectionFactory;

public class AvaliacaoDAO {

	/**
	 * Cadastra uma nova avaliação no banco de dados.
	 * 
	 * @param avaliacao Objeto Avaliacao a ser persistido.
	 * @return boolean true se inserido com sucesso, false caso contrário.
	 */
	public boolean cadastrar(Avaliacao avaliacao) {
		String sql = "INSERT INTO avaliacao (id_contratacao, cpf_cnpj_contratante, cpf_cnpj_prestador, nota, comentario, data_avaliacao) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);

			stmt.setInt(1, avaliacao.getId_contratacao());
			stmt.setString(2, avaliacao.getCpf_cnpj_contratante());
			stmt.setString(3, avaliacao.getCpf_cnpj_prestador());
			stmt.setInt(4, avaliacao.getNota());
			stmt.setString(5, avaliacao.getComentario());
			stmt.setDate(6, avaliacao.getData_avaliacao());

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("Erro ao cadastrar avaliação: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Busca uma avaliação pelo seu ID.
	 * 
	 * @param idAvaliacao ID da avaliação.
	 * @return Avaliacao encontrada ou null se não existir.
	 */
	public Avaliacao buscarPorId(int idAvaliacao) {
		String sql = "SELECT * FROM avaliacao WHERE id_avaliacao = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Avaliacao avaliacao = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idAvaliacao);
			rs = stmt.executeQuery();

			if (rs.next()) {
				avaliacao = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar avaliação por ID: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return avaliacao;
	}

	/**
	 * Busca a avaliação referente a uma contratação específica.
	 * 
	 * @param idContratacao ID da contratação.
	 * @return Avaliacao se existir, ou null se a contratação ainda não tiver sido avaliada.
	 */
	public Avaliacao buscarPorContratacao(int idContratacao) {
		String sql = "SELECT * FROM avaliacao WHERE id_contratacao = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Avaliacao avaliacao = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idContratacao);
			rs = stmt.executeQuery();

			if (rs.next()) {
				avaliacao = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar avaliação por contratação: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return avaliacao;
	}

	/**
	 * Lista todas as avaliações recebidas por um prestador específico.
	 * 
	 * @param cpfCnpjPrestador CPF/CNPJ do prestador avaliado.
	 * @return Lista de avaliações recebidas.
	 */
	public List<Avaliacao> listarPorPrestador(String cpfCnpjPrestador) {
		String sql = "SELECT * FROM avaliacao WHERE cpf_cnpj_prestador = ? ORDER BY data_avaliacao DESC, id_avaliacao DESC";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Avaliacao> lista = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpjPrestador);
			rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(mapearResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar avaliações por prestador: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return lista;
	}

	/**
	 * Lista todas as avaliações feitas por um contratante específico.
	 * 
	 * @param cpfCnpjContratante CPF/CNPJ do contratante autor da avaliação.
	 * @return Lista de avaliações realizadas.
	 */
	public List<Avaliacao> listarPorContratante(String cpfCnpjContratante) {
		String sql = "SELECT * FROM avaliacao WHERE cpf_cnpj_contratante = ? ORDER BY data_avaliacao DESC";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Avaliacao> lista = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpjContratante);
			rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(mapearResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar avaliações por contratante: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return lista;
	}

	/**
	 * Calcula a nota média recebida por um prestador em suas avaliações.
	 * 
	 * @param cpfCnpjPrestador CPF/CNPJ do prestador.
	 * @return Média das notas (ex: 4.8), ou 0.0 caso não tenha avaliações.
	 */
	public double calcularMediaPrestador(String cpfCnpjPrestador) {
		String sql = "SELECT AVG(nota) AS media FROM avaliacao WHERE cpf_cnpj_prestador = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpjPrestador);
			rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getDouble("media");
			}
		} catch (SQLException e) {
			System.err.println("Erro ao calcular média de avaliações do prestador: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return 0.0;
	}

	/**
	 * Conta a quantidade total de avaliações recebidas por um prestador.
	 * 
	 * @param cpfCnpjPrestador CPF/CNPJ do prestador.
	 * @return Total de avaliações.
	 */
	public int contarAvaliacoesPrestador(String cpfCnpjPrestador) {
		String sql = "SELECT COUNT(*) AS total FROM avaliacao WHERE cpf_cnpj_prestador = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpjPrestador);
			rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("total");
			}
		} catch (SQLException e) {
			System.err.println("Erro ao contar avaliações do prestador: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return 0;
	}

	/**
	 * Remove uma avaliação pelo seu ID.
	 * 
	 * @param idAvaliacao ID da avaliação.
	 * @return boolean true se excluído com sucesso.
	 */
	public boolean deletar(int idAvaliacao) {
		String sql = "DELETE FROM avaliacao WHERE id_avaliacao = ?";
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idAvaliacao);

			int linhas = stmt.executeUpdate();
			return linhas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao deletar avaliação: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Método auxiliar para mapear ResultSet para objeto Avaliacao.
	 */
	private Avaliacao mapearResultSet(ResultSet rs) throws SQLException {
		Avaliacao a = new Avaliacao();
		a.setId_avaliacao(rs.getInt("id_avaliacao"));
		a.setId_contratacao(rs.getInt("id_contratacao"));
		a.setCpf_cnpj_contratante(rs.getString("cpf_cnpj_contratante"));
		a.setCpf_cnpj_prestador(rs.getString("cpf_cnpj_prestador"));
		a.setNota(rs.getInt("nota"));
		a.setComentario(rs.getString("comentario"));
		a.setData_avaliacao(rs.getDate("data_avaliacao"));
		return a;
	}
}
