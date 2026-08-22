package br.com.conectaeventos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.conectaeventos.model.Prestador;
import connection.ConnectionFactory;

public class PrestadorDAO {

	/**
	 * Cadastra um novo prestador no banco de dados.
	 * 
	 * @param prestador Objeto Prestador com os dados a serem salvos.
	 * @return boolean true se inserido com sucesso, false caso contrário.
	 */
	public boolean cadastrar(Prestador prestador) {
		String sql = "INSERT INTO prestador (cpf_cnpj, nome_prestador, email_prestador, senha_prestador, "
				+ "telefone, endereco, cidade, categoria, descricao, data_cadastro, situacao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);

			stmt.setString(1, prestador.getCpf_cnpj());
			stmt.setString(2, prestador.getNome_prestador());
			stmt.setString(3, prestador.getEmail_prestador());
			stmt.setString(4, prestador.getSenha_prestador());
			stmt.setString(5, prestador.getTelefone());
			stmt.setString(6, prestador.getEndereco());
			stmt.setString(7, prestador.getCidade());
			stmt.setString(8, prestador.getCategoria());
			stmt.setString(9, prestador.getDescricao());
			stmt.setDate(10, prestador.getData_cadastro());
			stmt.setString(11, prestador.getSituacao());

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("Erro ao cadastrar prestador: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Busca um prestador pelo seu ID.
	 * 
	 * @param idPrestador ID do prestador.
	 * @return Prestador encontrado ou null se não existir.
	 */
	public Prestador buscarPorId(int idPrestador) {
		String sql = "SELECT * FROM prestador WHERE id_prestador = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Prestador prestador = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idPrestador);
			rs = stmt.executeQuery();

			if (rs.next()) {
				prestador = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar prestador por ID: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return prestador;
	}

	/**
	 * Busca um prestador pelo seu CPF ou CNPJ.
	 * 
	 * @param cpfCnpj CPF ou CNPJ do prestador.
	 * @return Prestador encontrado ou null se não existir.
	 */
	public Prestador buscarPorCpfCnpj(String cpfCnpj) {
		String sql = "SELECT * FROM prestador WHERE cpf_cnpj = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Prestador prestador = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpj);
			rs = stmt.executeQuery();

			if (rs.next()) {
				prestador = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar prestador por CPF/CNPJ: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return prestador;
	}

	/**
	 * Busca um prestador pelo email.
	 * 
	 * @param email Email do prestador.
	 * @return Prestador encontrado ou null se não existir.
	 */
	public Prestador buscarPorEmail(String email) {
		String sql = "SELECT * FROM prestador WHERE email_prestador = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Prestador prestador = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, email);
			rs = stmt.executeQuery();

			if (rs.next()) {
				prestador = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar prestador por email: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return prestador;
	}

	/**
	 * Lista todos os prestadores cadastrados.
	 * 
	 * @return List<Prestador> lista contendo todos os prestadores.
	 */
	public List<Prestador> listarTodos() {
		String sql = "SELECT * FROM prestador ORDER BY nome_prestador ASC";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Prestador> lista = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(mapearResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar prestadores: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return lista;
	}

	/**
	 * Lista todos os prestadores com situação 'ATIVO'.
	 * 
	 * @return List<Prestador> lista contendo os prestadores ativos.
	 */
	public List<Prestador> listarAtivos() {
		String sql = "SELECT * FROM prestador WHERE situacao = 'ATIVO' ORDER BY nome_prestador ASC";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Prestador> lista = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(mapearResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar prestadores ativos: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return lista;
	}

	/**
	 * Realiza busca dinâmica de prestadores com base em termo, categoria e cidade.
	 * Retorna apenas prestadores com situação 'ATIVO'.
	 * 
	 * @param termo Termo para busca em nome ou descrição.
	 * @param categoria Categoria dos serviços prestados.
	 * @param cidade Cidade de atendimento.
	 * @return Lista de prestadores que atendem aos filtros.
	 */
	public List<Prestador> buscarPorFiltros(String termo, String categoria, String cidade) {
		StringBuilder sql = new StringBuilder("SELECT * FROM prestador WHERE situacao = 'ATIVO'");
		List<Object> parametros = new ArrayList<>();

		if (termo != null && !termo.trim().isEmpty()) {
			sql.append(" AND (LOWER(nome_prestador) LIKE ? OR LOWER(descricao) LIKE ?)");
			String likeTermo = "%" + termo.trim().toLowerCase() + "%";
			parametros.add(likeTermo);
			parametros.add(likeTermo);
		}

		if (categoria != null && !categoria.trim().isEmpty()) {
			sql.append(" AND LOWER(categoria) LIKE ?");
			parametros.add("%" + categoria.trim().toLowerCase() + "%");
		}

		if (cidade != null && !cidade.trim().isEmpty()) {
			sql.append(" AND LOWER(cidade) LIKE ?");
			parametros.add("%" + cidade.trim().toLowerCase() + "%");
		}

		sql.append(" ORDER BY nome_prestador ASC");

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Prestador> lista = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql.toString());

			for (int i = 0; i < parametros.size(); i++) {
				stmt.setObject(i + 1, parametros.get(i));
			}

			rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(mapearResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar prestadores com filtros: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return lista;
	}

	/**
	 * Atualiza os dados de um prestador existente com base no CPF/CNPJ.
	 * 
	 * @param prestador Objeto com as informações atualizadas.
	 * @return boolean true se atualizado com sucesso, false caso contrário.
	 */
	public boolean atualizar(Prestador prestador) {
		String sql = "UPDATE prestador SET nome_prestador = ?, email_prestador = ?, senha_prestador = ?, "
				+ "telefone = ?, endereco = ?, cidade = ?, categoria = ?, descricao = ?, data_cadastro = ?, situacao = ? WHERE cpf_cnpj = ?";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);

			stmt.setString(1, prestador.getNome_prestador());
			stmt.setString(2, prestador.getEmail_prestador());
			stmt.setString(3, prestador.getSenha_prestador());
			stmt.setString(4, prestador.getTelefone());
			stmt.setString(5, prestador.getEndereco());
			stmt.setString(6, prestador.getCidade());
			stmt.setString(7, prestador.getCategoria());
			stmt.setString(8, prestador.getDescricao());
			stmt.setDate(9, prestador.getData_cadastro());
			stmt.setString(10, prestador.getSituacao());
			stmt.setString(11, prestador.getCpf_cnpj());

			int linhasAfetadas = stmt.executeUpdate();
			return linhasAfetadas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao atualizar prestador: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Exclui um prestador do banco de dados com base no CPF/CNPJ.
	 * 
	 * @param cpfCnpj CPF ou CNPJ do prestador a ser excluído.
	 * @return boolean true se excluído com sucesso, false caso contrário.
	 */
	public boolean deletar(String cpfCnpj) {
		String sql = "DELETE FROM prestador WHERE cpf_cnpj = ?";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpj);

			int linhasAfetadas = stmt.executeUpdate();
			return linhasAfetadas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao deletar prestador: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Valida as credenciais de login do prestador.
	 * 
	 * @param email Email do prestador.
	 * @param senha Senha do prestador.
	 * @return Prestador autenticado ou null se credenciais forem inválidas.
	 */
	public Prestador autenticar(String email, String senha) {
		String sql = "SELECT * FROM prestador WHERE email_prestador = ? AND senha_prestador = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Prestador prestador = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, email);
			stmt.setString(2, senha);
			rs = stmt.executeQuery();

			if (rs.next()) {
				prestador = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao autenticar prestador: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return prestador;
	}

	/**
	 * Método utilitário para converter um registro ResultSet em um objeto Prestador.
	 */
	private Prestador mapearResultSet(ResultSet rs) throws SQLException {
		Prestador prestador = new Prestador();
		prestador.setId_prestador(rs.getInt("id_prestador"));
		prestador.setCpf_cnpj(rs.getString("cpf_cnpj"));
		prestador.setNome_prestador(rs.getString("nome_prestador"));
		prestador.setEmail_prestador(rs.getString("email_prestador"));
		prestador.setSenha_prestador(rs.getString("senha_prestador"));
		prestador.setTelefone(rs.getString("telefone"));
		prestador.setEndereco(rs.getString("endereco"));
		prestador.setCidade(rs.getString("cidade"));
		try {
			prestador.setCategoria(rs.getString("categoria"));
		} catch (SQLException e) {
			// Coluna opcional
		}
		try {
			prestador.setDescricao(rs.getString("descricao"));
		} catch (SQLException e) {
			// Coluna opcional
		}
		prestador.setData_cadastro(rs.getDate("data_cadastro"));
		prestador.setSituacao(rs.getString("situacao"));
		return prestador;
	}
}
