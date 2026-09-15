package ch.m450.bibliothek.test;

import ch.m450.bibliothek.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class T013Runner {
    public static void main(String[] args) throws Exception {
        int ausleihenVorher = countLoansForBook(2);
        boolean verfuegbarVorher = isBookAvailable(2);

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO ausleihe (buch_id, mitglied_id, ausleihdatum, faelligkeitsdatum) VALUES (?, ?, ?, ?)")) {
                    insert.setInt(1, 2);
                    insert.setInt(2, 1);
                    insert.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                    insert.setDate(4, java.sql.Date.valueOf(LocalDate.now().plusDays(28)));
                    insert.executeUpdate();
                }

                try (PreparedStatement failingUpdate = connection.prepareStatement(
                        "UPDATE buch SET spalte_die_nicht_existiert = FALSE WHERE buch_id = ?")) {
                    failingUpdate.setInt(1, 2);
                    failingUpdate.executeUpdate();
                }
                connection.commit();
                throw new IllegalStateException("T-013 fehlgeschlagen: Fehler wurde nicht ausgeloest.");
            } catch (Exception expected) {
                connection.rollback();
                System.out.println("Simulierter Fehler: " + expected.getClass().getSimpleName());
            }
        }

        int ausleihenNachher = countLoansForBook(2);
        boolean verfuegbarNachher = isBookAvailable(2);
        if (ausleihenNachher != ausleihenVorher || verfuegbarNachher != verfuegbarVorher) {
            throw new IllegalStateException("T-013 fehlgeschlagen: Rollback war nicht vollstaendig.");
        }

        System.out.println("T-013 bestanden");
        System.out.println("Ausleihen fuer Buch 2 unveraendert: " + ausleihenNachher);
        System.out.println("Buch 2 verfuegbar unveraendert: " + verfuegbarNachher);
    }

    private static int countLoansForBook(int buchId) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM ausleihe WHERE buch_id = ?")) {
            statement.setInt(1, buchId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    private static boolean isBookAvailable(int buchId) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT verfuegbar FROM buch WHERE buch_id = ?")) {
            statement.setInt(1, buchId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }
        }
    }
}