package br.com.conectaeventos.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por centralizar a conexão com o banco MySQL
 * (conecta_evento). Todas as DAOs devem obter a Connection por aqui,
 * evitando repetir a configuração em cada classe.
 */
public class ConexaoBD {

    // TODO: ajustar usuário/senha conforme a configuração local do MySQL de cada máquina.
    private static final String URL = "jdbc:mysql://localhost:3306/conecta_evento?useSSL=false&serverTimezone=America/Sao_Paulo";
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    private ConexaoBD() {
        // classe utilitária: não deve ser instanciada
    }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}