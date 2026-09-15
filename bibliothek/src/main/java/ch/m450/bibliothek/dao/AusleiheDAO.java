package ch.m450.bibliothek.dao;

import ch.m450.bibliothek.db.DBConnection;

import java.sql.*;
import java.time.LocalDate;

public class AusleiheDAO {

    /** Wird geworfen, wenn ein Buch ausgeliehen werden soll, das nicht verfügbar ist. */
    public static class BuchNichtVerfuegbarException extends Exception {
        public BuchNichtVerfuegbarException(String message) { super(message); }
    }

    /**
     * Transaktion: Buch ausleihen.
     * Schritte (müssen atomar sein, ACID):
     *   1. Prüfen, ob das Buch verfügbar ist (SELECT ... FOR UPDATE sperrt die Zeile)
     *   2. Ausleihe-Datensatz einfügen
     *   3. Buch als nicht verfügbar markieren
     * Schlägt einer der Schritte fehl, wird die gesamte Transaktion zurückgerollt (rollback),
     * sodass kein inkonsistenter Zwischenzustand in der DB verbleibt.
     */
    public int ausleihen(int buchId, int mitgliedId, LocalDate ausleihdatum, LocalDate faelligkeitsdatum)
            throws SQLException, BuchNichtVerfuegbarException {

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // 1. Verfügbarkeit prüfen und Zeile sperren
            boolean verfuegbar;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT verfuegbar FROM buch WHERE buch_id = ? FOR UPDATE")) {
                ps.setInt(1, buchId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Buch mit ID " + buchId + " existiert nicht.");
                    verfuegbar = rs.getBoolean("verfuegbar");
                }
            }
            if (!verfuegbar) {
                con.rollback();
                throw new BuchNichtVerfuegbarException("Buch " + buchId + " ist aktuell nicht verfügbar.");
            }

            // 2. Ausleihe einfügen
            int ausleiheId;
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO ausleihe (buch_id, mitglied_id, ausleihdatum, faelligkeitsdatum) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, buchId);
                ps.setInt(2, mitgliedId);
                ps.setDate(3, Date.valueOf(ausleihdatum));
                ps.setDate(4, Date.valueOf(faelligkeitsdatum));
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    ausleiheId = rs.getInt(1);
                }
            }

            // 3. Buch als ausgeliehen markieren
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE buch SET verfuegbar = FALSE WHERE buch_id = ?")) {
                ps.setInt(1, buchId);
                ps.executeUpdate();
            }

            con.commit();
            return ausleiheId;

        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    /**
     * Transaktion: Buch zurückgeben.
     *   1. rueckgabedatum setzen
     *   2. Buch wieder als verfügbar markieren
     */
    public void zurueckgeben(int ausleiheId, int buchId, LocalDate rueckgabedatum) throws SQLException {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE ausleihe SET rueckgabedatum = ? WHERE ausleihe_id = ?")) {
                ps.setDate(1, Date.valueOf(rueckgabedatum));
                ps.setInt(2, ausleiheId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE buch SET verfuegbar = TRUE WHERE buch_id = ?")) {
                ps.setInt(1, buchId);
                ps.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }
}
