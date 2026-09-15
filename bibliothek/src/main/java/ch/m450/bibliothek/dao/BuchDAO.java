package ch.m450.bibliothek.dao;

import ch.m450.bibliothek.db.DBConnection;
import ch.m450.bibliothek.model.Buch;

import java.sql.*;

public class BuchDAO {

    /** Create: fügt ein neues Buch ein. Wirft SQLIntegrityConstraintViolationException bei doppelter ISBN. */
    public int erstellen(String titel, String autor, String isbn) throws SQLException {
        String sql = "INSERT INTO buch (titel, autor, isbn, verfuegbar) VALUES (?, ?, ?, TRUE)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, titel);
            ps.setString(2, autor);
            ps.setString(3, isbn);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    /** Read: liest ein Buch anhand der ID. Gibt null zurück, wenn nicht gefunden. */
    public Buch lesen(int buchId) throws SQLException {
        String sql = "SELECT * FROM buch WHERE buch_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, buchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Buch(rs.getInt("buch_id"), rs.getString("titel"),
                            rs.getString("autor"), rs.getString("isbn"), rs.getBoolean("verfuegbar"));
                }
            }
        }
        return null;
    }

    /** Update: ändert Titel/Autor eines Buches. */
    public void aktualisieren(int buchId, String neuerTitel, String neuerAutor) throws SQLException {
        String sql = "UPDATE buch SET titel = ?, autor = ? WHERE buch_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, neuerTitel);
            ps.setString(2, neuerAutor);
            ps.setInt(3, buchId);
            ps.executeUpdate();
        }
    }

    /**
     * Delete: löscht ein Buch. Schlägt mit SQLIntegrityConstraintViolationException
     * fehl, falls noch Ausleihen auf dieses Buch verweisen (FK RESTRICT).
     */
    public void loeschen(int buchId) throws SQLException {
        String sql = "DELETE FROM buch WHERE buch_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, buchId);
            ps.executeUpdate();
        }
    }
}
