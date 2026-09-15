package ch.m450.bibliothek.test;

import ch.m450.bibliothek.dao.AusleiheDAO;
import ch.m450.bibliothek.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class T012Runner {
    public static void main(String[] args) throws Exception {
        int ausleihenVorher = countLoansForBook(1);
        try {
            new AusleiheDAO().ausleihen(1, 2, LocalDate.now(), LocalDate.now().plusDays(28));
            throw new IllegalStateException("T-012 fehlgeschlagen: Zweite Ausleihe wurde zugelassen.");
        } catch (AusleiheDAO.BuchNichtVerfuegbarException expected) {
            int ausleihenNachher = countLoansForBook(1);
            if (ausleihenNachher != ausleihenVorher) {
                throw new IllegalStateException("T-012 fehlgeschlagen: Neuer Ausleihe-Datensatz wurde angelegt.");
            }
            System.out.println("T-012 bestanden");
            System.out.println("Erwartete Exception: " + expected.getClass().getSimpleName());
            System.out.println("Ausleihen fuer Buch 1 unveraendert: " + ausleihenNachher);
        }
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
}