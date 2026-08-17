package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static String getUrl() {
        String dbUrl = System.getenv("DB_URL");
        if (dbUrl != null && !dbUrl.trim().isEmpty()) {
            return dbUrl.trim();
        }

        String host = System.getenv("DB_HOST");
        if (host == null || host.trim().isEmpty()) {
            host = "localhost";
        }

        String port = System.getenv("DB_PORT");
        if (port == null || port.trim().isEmpty()) {
            port = "3306";
        }

        String dbName = System.getenv("DB_NAME");
        if (dbName == null || dbName.trim().isEmpty()) {
            dbName = "conectaeventos";
        }

        return "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?useSSL=false&allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=UTC&characterEncoding=UTF-8";
    }

    private static String getUser() {
        String user = System.getenv("DB_USER");
        if (user != null && !user.trim().isEmpty()) {
            return user.trim();
        }
        return "root";
    }

    private static String getPass() {
        String pass = System.getenv("DB_PASS");
        if (pass != null) {
            return pass;
        }
        String rootPass = System.getenv("MYSQL_ROOT_PASSWORD");
        if (rootPass != null) {
            return rootPass;
        }
        return "";
    }

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(getUrl(), getUser(), getPass());
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados (" + getUrl() + "): " + e.getMessage(), e);
        }
    }

    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeConnection(Connection con, PreparedStatement stmt) {
        closeConnection(con);
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeConnection(Connection con, PreparedStatement stmt, ResultSet rs) {
        closeConnection(con, stmt);
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
