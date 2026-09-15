package ch.m450.bibliothek.dao;

import ch.m450.bibliothek.db.DBConnection;
import ch.m450.bibliothek.model.Mitglied;

import java.sql.*;

public class MitgliedDAO {

    /** Create: fügt ein neues Mitglied ein. Wirft SQLIntegrityConstraintViolationException bei doppelter E-Mail. */
    public int erstellen(String name, String email, int ausleihlimit) throws SQLException {
        String sql = "INSERT INTO mitglied (name, email, ausleihlimit) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, ausleihlimit);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public Mitglied lesen(int mitgliedId) throws SQLException {
        String sql = "SELECT * FROM mitglied WHERE mitglied_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, mitgliedId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Mitglied(rs.getInt("mitglied_id"), rs.getString("name"),
                            rs.getString("email"), rs.getInt("ausleihlimit"));
                }
            }
        }
        return null;
    }

    /**
     * Delete: löscht ein Mitglied. Schlägt mit SQLIntegrityConstraintViolationException
     * fehl, falls noch Ausleihen auf dieses Mitglied verweisen (FK RESTRICT).
     */
    public void loeschen(int mitgliedId) throws SQLException {
        String sql = "DELETE FROM mitglied WHERE mitglied_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, mitgliedId);
            ps.executeUpdate();
        }
    }
}
