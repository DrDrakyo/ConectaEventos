package br.com.conectaeventos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.conectaeventos.model.PortfolioItem;
import connection.ConnectionFactory;

public class PortfolioDAO {

	/**
	 * Cadastra um novo item de portfólio no banco de dados.
	 * 
	 * @param item Objeto PortfolioItem a ser persistido.
	 * @return boolean true se inserido com sucesso, false caso contrário.
	 */
	public boolean cadastrar(PortfolioItem item) {
		String sql = "INSERT INTO portfolio_item (cpf_cnpj_prestador, titulo, descricao, imagem_url, data_publicacao) "
				+ "VALUES (?, ?, ?, ?, ?)";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);

			stmt.setString(1, item.getCpf_cnpj_prestador());
			stmt.setString(2, item.getTitulo());
			stmt.setString(3, item.getDescricao());
			stmt.setString(4, item.getImagem_url());
			stmt.setDate(5, item.getData_publicacao());

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("Erro ao cadastrar item de portfólio: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Busca um item de portfólio pelo seu ID.
	 * 
	 * @param idPortfolio ID do item.
	 * @return PortfolioItem encontrado ou null se não existir.
	 */
	public PortfolioItem buscarPorId(int idPortfolio) {
		String sql = "SELECT * FROM portfolio_item WHERE id_portfolio = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PortfolioItem item = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idPortfolio);
			rs = stmt.executeQuery();

			if (rs.next()) {
				item = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar item de portfólio por ID: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return item;
	}

	/**
	 * Lista todos os itens de portfólio de um prestador de serviços.
	 * 
	 * @param cpfCnpjPrestador CPF/CNPJ do prestador.
	 * @return Lista de itens de portfólio do prestador.
	 */
	public List<PortfolioItem> listarPorPrestador(String cpfCnpjPrestador) {
		String sql = "SELECT * FROM portfolio_item WHERE cpf_cnpj_prestador = ? ORDER BY data_publicacao DESC, id_portfolio DESC";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<PortfolioItem> lista = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpjPrestador);
			rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(mapearResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar itens de portfólio: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return lista;
	}

	/**
	 * Atualiza os dados de um item de portfólio existente.
	 * 
	 * @param item Objeto com os dados atualizados.
	 * @return boolean true se atualizado com sucesso.
	 */
	public boolean atualizar(PortfolioItem item) {
		String sql = "UPDATE portfolio_item SET titulo = ?, descricao = ?, imagem_url = ?, data_publicacao = ? "
				+ "WHERE id_portfolio = ? AND cpf_cnpj_prestador = ?";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);

			stmt.setString(1, item.getTitulo());
			stmt.setString(2, item.getDescricao());
			stmt.setString(3, item.getImagem_url());
			stmt.setDate(4, item.getData_publicacao());
			stmt.setInt(5, item.getId_portfolio());
			stmt.setString(6, item.getCpf_cnpj_prestador());

			int linhas = stmt.executeUpdate();
			return linhas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao atualizar item de portfólio: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Deleta um item de portfólio pelo seu ID.
	 * 
	 * @param idPortfolio ID do item a ser excluído.
	 * @return boolean true se excluído com sucesso.
	 */
	public boolean deletar(int idPortfolio) {
		String sql = "DELETE FROM portfolio_item WHERE id_portfolio = ?";
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idPortfolio);

			int linhas = stmt.executeUpdate();
			return linhas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao deletar item de portfólio: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Deleta todos os itens de portfólio vinculados a um prestador.
	 * 
	 * @param cpfCnpjPrestador CPF/CNPJ do prestador.
	 * @return boolean true se a operação for concluída com sucesso.
	 */
	public boolean deletarPorPrestador(String cpfCnpjPrestador) {
		String sql = "DELETE FROM portfolio_item WHERE cpf_cnpj_prestador = ?";
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, cpfCnpjPrestador);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("Erro ao deletar itens de portfólio do prestador: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Mapeia o registro ResultSet para o objeto PortfolioItem.
	 */
	private PortfolioItem mapearResultSet(ResultSet rs) throws SQLException {
		PortfolioItem item = new PortfolioItem();
		item.setId_portfolio(rs.getInt("id_portfolio"));
		item.setCpf_cnpj_prestador(rs.getString("cpf_cnpj_prestador"));
		item.setTitulo(rs.getString("titulo"));
		item.setDescricao(rs.getString("descricao"));
		item.setImagem_url(rs.getString("imagem_url"));
		item.setData_publicacao(rs.getDate("data_publicacao"));
		return item;
	}
}
