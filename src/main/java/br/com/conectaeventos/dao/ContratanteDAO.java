package br.com.conectaeventos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.conectaeventos.model.Contratante;
import connection.ConnectionFactory;

public class ContratanteDAO {

	/**
	 * Cadastra um novo contratante no banco de dados.
	 * 
	 * @param contratante Objeto Contratante com os dados a serem salvos.
	 * @return boolean true se inserido com sucesso, false caso contrário.
	 */
	public boolean cadastrar(Contratante contratante) {
		String sql = "INSERT INTO contratante (cpf_cnpj, nome_contratante, email_contratante, senha_contratante, "
				+ "telefone, endereco, cidade, data_cadastro, situacao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);

			stmt.setString(1, contratante.getCpf_cnpj());
			stmt.setString(2, contratante.getNome_contratante());
			stmt.setString(3, contratante.getEmail_contratante());
			stmt.setString(4, contratante.getSenha_contratante());
			stmt.setString(5, contratante.getTelefone());
			stmt.setString(6, contratante.getEndereco());
			stmt.setString(7, contratante.getCidade());
			stmt.setDate(8, contratante.getData_cadastro());
			stmt.setString(9, contratante.getSituacao());

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("Erro ao cadastrar contratante: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Busca um contratante pelo seu CPF ou CNPJ.
	 * 
	 * @param cpfCnpj CPF ou CNPJ do contratante.
	 * @return Contratante encontrado ou null se não existir.
	 */
	public Contratante buscarPorCpfCnpj(String cpfCnpj) {
		String sql = "SELECT * FROM contratante WHERE cpf_cnpj = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Contratante contratante = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpj);
			rs = stmt.executeQuery();

			if (rs.next()) {
				contratante = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar contratante por CPF/CNPJ: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return contratante;
	}

	/**
	 * Busca um contratante pelo email.
	 * 
	 * @param email Email do contratante.
	 * @return Contratante encontrado ou null se não existir.
	 */
	public Contratante buscarPorEmail(String email) {
		String sql = "SELECT * FROM contratante WHERE email_contratante = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Contratante contratante = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, email);
			rs = stmt.executeQuery();

			if (rs.next()) {
				contratante = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar contratante por email: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return contratante;
	}

	/**
	 * Lista todos os contratantes cadastrados.
	 * 
	 * @return List<Contratante> lista contendo todos os contratantes.
	 */
	public List<Contratante> listarTodos() {
		String sql = "SELECT * FROM contratante ORDER BY nome_contratante ASC";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Contratante> lista = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(mapearResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar contratantes: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return lista;
	}

	/**
	 * Atualiza os dados de um contratante existente com base no CPF/CNPJ.
	 * 
	 * @param contratante Objeto com as informações atualizadas.
	 * @return boolean true se atualizado com sucesso, false caso contrário.
	 */
	public boolean atualizar(Contratante contratante) {
		String sql = "UPDATE contratante SET nome_contratante = ?, email_contratante = ?, senha_contratante = ?, "
				+ "telefone = ?, endereco = ?, cidade = ?, data_cadastro = ?, situacao = ? WHERE cpf_cnpj = ?";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);

			stmt.setString(1, contratante.getNome_contratante());
			stmt.setString(2, contratante.getEmail_contratante());
			stmt.setString(3, contratante.getSenha_contratante());
			stmt.setString(4, contratante.getTelefone());
			stmt.setString(5, contratante.getEndereco());
			stmt.setString(6, contratante.getCidade());
			stmt.setDate(7, contratante.getData_cadastro());
			stmt.setString(8, contratante.getSituacao());
			stmt.setString(9, contratante.getCpf_cnpj());

			int linhasAfetadas = stmt.executeUpdate();
			return linhasAfetadas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao atualizar contratante: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Exclui um contratante do banco de dados com base no CPF/CNPJ.
	 * 
	 * @param cpfCnpj CPF ou CNPJ do contratante a ser excluído.
	 * @return boolean true se excluído com sucesso, false caso contrário.
	 */
	public boolean deletar(String cpfCnpj) {
		String sql = "DELETE FROM contratante WHERE cpf_cnpj = ?";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpj);

			int linhasAfetadas = stmt.executeUpdate();
			return linhasAfetadas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao deletar contratante: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Valida as credenciais de login do contratante.
	 * 
	 * @param email Email do contratante.
	 * @param senha Senha do contratante.
	 * @return Contratante autenticado ou null se credenciais forem inválidas.
	 */
	public Contratante autenticar(String email, String senha) {
		String sql = "SELECT * FROM contratante WHERE email_contratante = ? AND senha_contratante = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Contratante contratante = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, email);
			stmt.setString(2, senha);
			rs = stmt.executeQuery();

			if (rs.next()) {
				contratante = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao autenticar contratante: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return contratante;
	}

	/**
	 * Método utilitário para converter um registro ResultSet em um objeto Contratante.
	 */
	private Contratante mapearResultSet(ResultSet rs) throws SQLException {
		Contratante contratante = new Contratante();
		contratante.setCpf_cnpj(rs.getString("cpf_cnpj"));
		contratante.setNome_contratante(rs.getString("nome_contratante"));
		contratante.setEmail_contratante(rs.getString("email_contratante"));
		contratante.setSenha_contratante(rs.getString("senha_contratante"));
		contratante.setTelefone(rs.getString("telefone"));
		contratante.setEndereco(rs.getString("endereco"));
		contratante.setCidade(rs.getString("cidade"));
		contratante.setData_cadastro(rs.getDate("data_cadastro"));
		contratante.setSituacao(rs.getString("situacao"));
		return contratante;
	}
}
