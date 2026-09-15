package ch.m450.bibliothek.test;

import ch.m450.bibliothek.dao.BuchDAO;
import ch.m450.bibliothek.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class T021Runner {
    public static void main(String[] args) throws Exception {
        String isbn = "9780132350884";
        int buecherVorher = countBooksWithIsbn(isbn);
        if (buecherVorher != 1) {
            throw new IllegalStateException("T-021 kann nicht starten: Test-ISBN fehlt oder ist nicht eindeutig.");
        }

        try {
            new BuchDAO().erstellen("Testbuch", "Testautor", isbn);
            throw new IllegalStateException("T-021 fehlgeschlagen: Doppelte ISBN wurde zugelassen.");
        } catch (java.sql.SQLIntegrityConstraintViolationException expected) {
            int buecherNachher = countBooksWithIsbn(isbn);
            if (buecherNachher != buecherVorher) {
                throw new IllegalStateException("T-021 fehlgeschlagen: Dublette wurde gespeichert.");
            }
            System.out.println("T-021 bestanden");
            System.out.println("Erwartete Exception: " + expected.getClass().getSimpleName());
            System.out.println("Buecher mit dieser ISBN unveraendert: " + buecherNachher);
        }
    }

    private static int countBooksWithIsbn(String isbn) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM buch WHERE isbn = ?")) {
            statement.setString(1, isbn);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }
}