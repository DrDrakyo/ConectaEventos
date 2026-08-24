package br.com.conectaeventos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.conectaeventos.model.Administrador;
import connection.ConnectionFactory;

public class AdministradorDAO {

	/**
	 * Cadastra um novo administrador no banco de dados.
	 * 
	 * @param adm Objeto Administrador com os dados a serem salvos.
	 * @return boolean true se inserido com sucesso, false caso contrário.
	 */
	public boolean cadastrar(Administrador adm) {
		String sql = "INSERT INTO administrador (nome_administrador, email_administrador, senha_administrador, data_cadastro, situacao) "
				+ "VALUES (?, ?, ?, ?, ?)";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);

			stmt.setString(1, adm.getNome_administrador());
			stmt.setString(2, adm.getEmail_administrador());
			stmt.setString(3, adm.getSenha_administrador());
			stmt.setDate(4, adm.getData_cadastro());
			stmt.setString(5, adm.getSituacao());

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("Erro ao cadastrar administrador: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Busca um administrador pelo seu ID.
	 * 
	 * @param id ID do administrador.
	 * @return Administrador encontrado ou null se não existir.
	 */
	public Administrador buscarPorId(int id) {
		String sql = "SELECT * FROM administrador WHERE id_administrador = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Administrador adm = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();

			if (rs.next()) {
				adm = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar administrador por ID: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return adm;
	}

	/**
	 * Busca um administrador pelo seu e-mail.
	 * 
	 * @param email E-mail do administrador.
	 * @return Administrador encontrado ou null se não existir.
	 */
	public Administrador buscarPorEmail(String email) {
		String sql = "SELECT * FROM administrador WHERE email_administrador = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Administrador adm = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, email);
			rs = stmt.executeQuery();

			if (rs.next()) {
				adm = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar administrador por email: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return adm;
	}

	/**
	 * Lista todos os administradores cadastrados.
	 * 
	 * @return List<Administrador> lista contendo todos os administradores.
	 */
	public List<Administrador> listarTodos() {
		String sql = "SELECT * FROM administrador ORDER BY nome_administrador ASC";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Administrador> lista = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(mapearResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar administradores: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return lista;
	}

	/**
	 * Atualiza os dados de um administrador existente com base no ID.
	 * 
	 * @param adm Objeto com as informações atualizadas.
	 * @return boolean true se atualizado com sucesso, false caso contrário.
	 */
	public boolean atualizar(Administrador adm) {
		String sql = "UPDATE administrador SET nome_administrador = ?, email_administrador = ?, senha_administrador = ?, "
				+ "data_cadastro = ?, situacao = ? WHERE id_administrador = ?";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);

			stmt.setString(1, adm.getNome_administrador());
			stmt.setString(2, adm.getEmail_administrador());
			stmt.setString(3, adm.getSenha_administrador());
			stmt.setDate(4, adm.getData_cadastro());
			stmt.setString(5, adm.getSituacao());
			stmt.setInt(6, adm.getId_administrador());

			int linhasAfetadas = stmt.executeUpdate();
			return linhasAfetadas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao atualizar administrador: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Exclui um administrador do banco de dados com base no ID.
	 * 
	 * @param id ID do administrador a ser excluído.
	 * @return boolean true se excluído com sucesso, false caso contrário.
	 */
	public boolean deletar(int id) {
		String sql = "DELETE FROM administrador WHERE id_administrador = ?";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, id);

			int linhasAfetadas = stmt.executeUpdate();
			return linhasAfetadas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao deletar administrador: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Valida as credenciais de login do administrador.
	 * 
	 * @param email Email do administrador.
	 * @param senha Senha do administrador.
	 * @return Administrador autenticado ou null se credenciais forem inválidas.
	 */
	public Administrador autenticar(String email, String senha) {
		String sql = "SELECT * FROM administrador WHERE email_administrador = ? AND senha_administrador = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Administrador adm = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, email);
			stmt.setString(2, senha);
			rs = stmt.executeQuery();

			if (rs.next()) {
				adm = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao autenticar administrador: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return adm;
	}

	/**
	 * Método utilitário para converter um registro ResultSet em um objeto Administrador.
	 */
	private Administrador mapearResultSet(ResultSet rs) throws SQLException {
		Administrador adm = new Administrador();
		adm.setId_administrador(rs.getInt("id_administrador"));
		adm.setNome_administrador(rs.getString("nome_administrador"));
		adm.setEmail_administrador(rs.getString("email_administrador"));
		adm.setSenha_administrador(rs.getString("senha_administrador"));
		adm.setData_cadastro(rs.getDate("data_cadastro"));
		adm.setSituacao(rs.getString("situacao"));
		return adm;
	}
}
