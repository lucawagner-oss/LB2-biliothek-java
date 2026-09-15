package ch.m450.bibliothek.test;

import ch.m450.bibliothek.dao.AusleiheDAO;
import ch.m450.bibliothek.dao.BuchDAO;
import ch.m450.bibliothek.db.DBConnection;
import ch.m450.bibliothek.model.Buch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class T011Runner {
    public static void main(String[] args) throws Exception {
        int ausleiheId = new AusleiheDAO().ausleihen(
                1, 1, LocalDate.now(), LocalDate.now().plusDays(28));
        Buch buch = new BuchDAO().lesen(1);

        int offeneAusleihen = 0;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM ausleihe WHERE ausleihe_id = ? AND rueckgabedatum IS NULL")) {
            statement.setInt(1, ausleiheId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                offeneAusleihen = resultSet.getInt(1);
            }
        }

        if (buch == null || buch.isVerfuegbar() || offeneAusleihen != 1) {
            throw new IllegalStateException("T-011 fehlgeschlagen: Datenbankzustand ist inkonsistent.");
        }

        System.out.println("T-011 bestanden");
        System.out.println("Ausleihe-ID: " + ausleiheId);
        System.out.println("Offene Ausleihe vorhanden: true");
        System.out.println("Buch 1 verfuegbar: " + buch.isVerfuegbar());
    }
}