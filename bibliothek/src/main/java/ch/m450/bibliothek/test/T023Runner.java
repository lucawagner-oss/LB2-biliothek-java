package ch.m450.bibliothek.test;

import ch.m450.bibliothek.dao.AusleiheDAO;
import ch.m450.bibliothek.dao.BuchDAO;
import ch.m450.bibliothek.db.DBConnection;
import ch.m450.bibliothek.model.Buch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class T023Runner {
    public static void main(String[] args) throws Exception {
        ensureOpenLoanForBook(1);

        try {
            new BuchDAO().loeschen(1);
            throw new IllegalStateException("T-023 fehlgeschlagen: Buch mit aktiver Ausleihe wurde geloescht.");
        } catch (java.sql.SQLIntegrityConstraintViolationException expected) {
            Buch buch = new BuchDAO().lesen(1);
            if (buch == null) {
                throw new IllegalStateException("T-023 fehlgeschlagen: Buch ist nicht mehr vorhanden.");
            }
            System.out.println("T-023 bestanden");
            System.out.println("Erwartete Exception: " + expected.getClass().getSimpleName());
            System.out.println("Buch 1 weiterhin vorhanden: true");
        }
    }

    private static void ensureOpenLoanForBook(int buchId) throws Exception {
        if (hasOpenLoan(buchId)) {
            return;
        }
        new AusleiheDAO().ausleihen(buchId, 1, LocalDate.now(), LocalDate.now().plusDays(28));
    }

    private static boolean hasOpenLoan(int buchId) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM ausleihe WHERE buch_id = ? AND rueckgabedatum IS NULL")) {
            statement.setInt(1, buchId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }
}