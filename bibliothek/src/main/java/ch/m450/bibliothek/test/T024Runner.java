package ch.m450.bibliothek.test;

import ch.m450.bibliothek.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class T024Runner {
    public static void main(String[] args) throws Exception {
        int eintraegeVorher = countInvalidLoans();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO ausleihe (buch_id, mitglied_id, ausleihdatum, faelligkeitsdatum) VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, 2);
            statement.setInt(2, 1);
            statement.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            statement.setDate(4, java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
            statement.executeUpdate();
            throw new IllegalStateException("T-024 fehlgeschlagen: Ungueltiges Faelligkeitsdatum wurde zugelassen.");
        } catch (SQLException expected) {
            int eintraegeNachher = countInvalidLoans();
            if (eintraegeNachher != eintraegeVorher) {
                throw new IllegalStateException("T-024 fehlgeschlagen: Ungueltiger Datensatz wurde gespeichert.");
            }
            System.out.println("T-024 bestanden");
            System.out.println("Erwartete Exception: " + expected.getClass().getSimpleName());
            System.out.println("Ungueltige Eintraege unveraendert: " + eintraegeNachher);
        }
    }

    private static int countInvalidLoans() throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM ausleihe WHERE faelligkeitsdatum <= ausleihdatum")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }
}