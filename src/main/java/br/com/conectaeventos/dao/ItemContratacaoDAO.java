package br.com.conectaeventos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.conectaeventos.model.ItemContratacao;
import connection.ConnectionFactory;

public class ItemContratacaoDAO {

	/**
	 * Cadastra um novo item de contratação no banco de dados.
	 * 
	 * @param item Objeto ItemContratacao a ser inserido.
	 * @return boolean true se inserido com sucesso, false caso contrário.
	 */
	public boolean cadastrar(ItemContratacao item) {
		String sql = "INSERT INTO item_contratacao (id_contratacao, descricao_item, quantidade, valor_unitario, valor_total) "
				+ "VALUES (?, ?, ?, ?, ?)";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);

			stmt.setInt(1, item.getId_contratacao());
			stmt.setString(2, item.getDescricao_item());
			stmt.setInt(3, item.getQuantidade());
			stmt.setDouble(4, item.getValor_unitario());
			stmt.setDouble(5, item.getValor_total());

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("Erro ao cadastrar item de contratação: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Cadastra uma lista de itens para uma contratação.
	 * 
	 * @param itens Lista de itens a serem inseridos.
	 * @return boolean true se todos forem inseridos com sucesso, false caso ocorra algum erro.
	 */
	public boolean cadastrarItens(List<ItemContratacao> itens) {
		if (itens == null || itens.isEmpty()) {
			return true;
		}

		String sql = "INSERT INTO item_contratacao (id_contratacao, descricao_item, quantidade, valor_unitario, valor_total) "
				+ "VALUES (?, ?, ?, ?, ?)";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			con.setAutoCommit(false);
			stmt = con.prepareStatement(sql);

			for (ItemContratacao item : itens) {
				stmt.setInt(1, item.getId_contratacao());
				stmt.setString(2, item.getDescricao_item());
				stmt.setInt(3, item.getQuantidade());
				stmt.setDouble(4, item.getValor_unitario());
				stmt.setDouble(5, item.getValor_total());
				stmt.addBatch();
			}

			stmt.executeBatch();
			con.commit();
			return true;
		} catch (SQLException e) {
			System.err.println("Erro ao cadastrar lote de itens de contratação: " + e.getMessage());
			e.printStackTrace();
			if (con != null) {
				try {
					con.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Lista todos os itens pertencentes a uma contratação específica.
	 * 
	 * @param idContratacao ID da contratação.
	 * @return List<ItemContratacao> lista de itens encontrados.
	 */
	public List<ItemContratacao> listarPorContratacao(int idContratacao) {
		String sql = "SELECT * FROM item_contratacao WHERE id_contratacao = ? ORDER BY id_item ASC";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<ItemContratacao> lista = new ArrayList<>();

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idContratacao);
			rs = stmt.executeQuery();

			while (rs.next()) {
				lista.add(mapearResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar itens da contratação " + idContratacao + ": " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return lista;
	}

	/**
	 * Busca um item específico pelo seu ID.
	 * 
	 * @param idItem ID do item.
	 * @return ItemContratacao ou null se não encontrado.
	 */
	public ItemContratacao buscarPorId(int idItem) {
		String sql = "SELECT * FROM item_contratacao WHERE id_item = ?";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ItemContratacao item = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idItem);
			rs = stmt.executeQuery();

			if (rs.next()) {
				item = mapearResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar item de contratação por ID: " + e.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(con, stmt, rs);
		}

		return item;
	}

	/**
	 * Atualiza os dados de um item de contratação existente.
	 * 
	 * @param item Objeto com as informações atualizadas.
	 * @return boolean true se atualizado com sucesso, false caso contrário.
	 */
	public boolean atualizar(ItemContratacao item) {
		String sql = "UPDATE item_contratacao SET descricao_item = ?, quantidade = ?, "
				+ "valor_unitario = ?, valor_total = ? WHERE id_item = ?";

		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);

			stmt.setString(1, item.getDescricao_item());
			stmt.setInt(2, item.getQuantidade());
			stmt.setDouble(3, item.getValor_unitario());
			stmt.setDouble(4, item.getValor_total());
			stmt.setInt(5, item.getId_item());

			int linhasAfetadas = stmt.executeUpdate();
			return linhasAfetadas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao atualizar item de contratação: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Exclui um item de contratação pelo seu ID.
	 * 
	 * @param idItem ID do item a ser excluído.
	 * @return boolean true se excluído com sucesso, false caso contrário.
	 */
	public boolean deletar(int idItem) {
		String sql = "DELETE FROM item_contratacao WHERE id_item = ?";
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idItem);

			int linhasAfetadas = stmt.executeUpdate();
			return linhasAfetadas > 0;
		} catch (SQLException e) {
			System.err.println("Erro ao deletar item de contratação: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Exclui todos os itens vinculados a uma contratação.
	 * 
	 * @param idContratacao ID da contratação.
	 * @return boolean true se a operação for concluída com sucesso.
	 */
	public boolean deletarPorContratacao(int idContratacao) {
		String sql = "DELETE FROM item_contratacao WHERE id_contratacao = ?";
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = ConnectionFactory.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, idContratacao);

			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("Erro ao deletar itens da contratação " + idContratacao + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			ConnectionFactory.closeConnection(con, stmt);
		}
	}

	/**
	 * Converte um registro do ResultSet em um objeto ItemContratacao.
	 */
	private ItemContratacao mapearResultSet(ResultSet rs) throws SQLException {
		ItemContratacao item = new ItemContratacao();
		item.setId_item(rs.getInt("id_item"));
		item.setId_contratacao(rs.getInt("id_contratacao"));
		item.setDescricao_item(rs.getString("descricao_item"));
		item.setQuantidade(rs.getInt("quantidade"));
		item.setValor_unitario(rs.getDouble("valor_unitario"));
		
		try {
			item.setValor_total(rs.getDouble("valor_total"));
		} catch (SQLException e) {
			// Fallback caso a coluna seja nomeada valor_item
			try {
				item.setValor_total(rs.getDouble("valor_item"));
			} catch (SQLException e2) {
				item.setValor_total(item.getQuantidade() * item.getValor_unitario());
			}
		}

		return item;
	}
}
