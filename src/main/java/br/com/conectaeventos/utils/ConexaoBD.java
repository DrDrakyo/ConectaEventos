package br.com.conectaeventos.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import connection.ConnectionFactory;

/**
 * Classe utilitária que atua como facade para o gerenciador de conexões ConnectionFactory.
 */
public class ConexaoBD {

	public static Connection getConnection() {
		return ConnectionFactory.getConnection();
	}

	public static void closeConnection(Connection con) {
		ConnectionFactory.closeConnection(con);
	}

	public static void closeConnection(Connection con, PreparedStatement stmt) {
		ConnectionFactory.closeConnection(con, stmt);
	}

	public static void closeConnection(Connection con, PreparedStatement stmt, ResultSet rs) {
		ConnectionFactory.closeConnection(con, stmt, rs);
	}
}
