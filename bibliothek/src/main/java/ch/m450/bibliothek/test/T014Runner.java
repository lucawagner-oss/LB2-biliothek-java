package ch.m450.bibliothek.test;

import ch.m450.bibliothek.dao.AusleiheDAO;
import ch.m450.bibliothek.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class T014Runner {
    public static void main(String[] args) throws Exception {
        int ausleiheId = findOpenLoanForBook(1);
        if (ausleiheId < 0) {
            throw new IllegalStateException("T-014 kann nicht starten: Keine offene Ausleihe fuer Buch 1 gefunden.");
        }

        new AusleiheDAO().zurueckgeben(ausleiheId, 1, LocalDate.now());

        boolean rueckgabeGesetzt = hasReturnDate(ausleiheId);
        boolean buchVerfuegbar = isBookAvailable(1);
        if (!rueckgabeGesetzt || !buchVerfuegbar) {
            throw new IllegalStateException("T-014 fehlgeschlagen: Rueckgabezustand ist inkonsistent.");
        }

        System.out.println("T-014 bestanden");
        System.out.println("Ausleihe-ID: " + ausleiheId);
        System.out.println("Rueckgabedatum gesetzt: true");
        System.out.println("Buch 1 verfuegbar: true");
    }

    private static int findOpenLoanForBook(int buchId) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT ausleihe_id FROM ausleihe WHERE buch_id = ? AND rueckgabedatum IS NULL ORDER BY ausleihe_id LIMIT 1")) {
            statement.setInt(1, buchId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : -1;
            }
        }
    }

    private static boolean hasReturnDate(int ausleiheId) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT rueckgabedatum IS NOT NULL FROM ausleihe WHERE ausleihe_id = ?")) {
            statement.setInt(1, ausleiheId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getBoolean(1);
            }
        }
    }

    private static boolean isBookAvailable(int buchId) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT verfuegbar FROM buch WHERE buch_id = ?")) {
            statement.setInt(1, buchId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getBoolean(1);
            }
        }
    }
}