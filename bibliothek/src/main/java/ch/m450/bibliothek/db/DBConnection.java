package ch.m450.bibliothek.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Stellt die Verbindung zur MySQL-Datenbank "bibliothek" her.
 * Verbindungsparameter zentral an einem Ort, damit Tests sie
 * leicht auf eine Test-DB umbiegen können.
 */
public class DBConnection {

    private static final String URL = envOrDefault(
            "DB_URL", "jdbc:mysql://localhost:3306/bibliothek?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    private static final String USER = envOrDefault("DB_USER", "biblio_user");
    private static final String PASSWORD = envOrDefault("DB_PASSWORD", "changeme");

    private static String envOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static Connection getConnection() throws SQLException {
        SQLException lastException = null;
        for (int attempt = 1; attempt <= 30; attempt++) {
            try {
                return DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException exception) {
                lastException = exception;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw exception;
                }
            }
        }
        throw lastException;
    }
}
