package ch.m450.bibliothek.test;

import ch.m450.bibliothek.dao.MitgliedDAO;
import ch.m450.bibliothek.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class T022Runner {
    public static void main(String[] args) throws Exception {
        int mitgliederVorher = countMembersWithEmail("anna.meier@example.ch");
        if (mitgliederVorher != 1) {
            throw new IllegalStateException("T-022 kann nicht starten: Testmitglied fehlt oder ist nicht eindeutig.");
        }

        try {
            new MitgliedDAO().erstellen("Anna Test", "anna.meier@example.ch", 5);
            throw new IllegalStateException("T-022 fehlgeschlagen: Doppelte E-Mail wurde zugelassen.");
        } catch (java.sql.SQLIntegrityConstraintViolationException expected) {
            int mitgliederNachher = countMembersWithEmail("anna.meier@example.ch");
            if (mitgliederNachher != mitgliederVorher) {
                throw new IllegalStateException("T-022 fehlgeschlagen: Dublette wurde gespeichert.");
            }
            System.out.println("T-022 bestanden");
            System.out.println("Erwartete Exception: " + expected.getClass().getSimpleName());
            System.out.println("Mitglieder mit dieser E-Mail unveraendert: " + mitgliederNachher);
        }
    }

    private static int countMembersWithEmail(String email) throws Exception {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM mitglied WHERE email = ?")) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }
}