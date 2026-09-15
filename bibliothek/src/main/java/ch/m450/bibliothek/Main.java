package ch.m450.bibliothek;

import ch.m450.bibliothek.dao.AusleiheDAO;
import ch.m450.bibliothek.dao.BuchDAO;
import ch.m450.bibliothek.dao.MitgliedDAO;
import ch.m450.bibliothek.model.Buch;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Kleine Konsolen-Demo. UI ist für diesen Auftrag nicht zwingend (siehe Arbeitsauftrag),
 * dient hier nur dazu, die DAO-Schicht manuell durchzuspielen.
 */
public class Main {
    public static void main(String[] args) {
        BuchDAO buchDAO = new BuchDAO();
        MitgliedDAO mitgliedDAO = new MitgliedDAO();
        AusleiheDAO ausleiheDAO = new AusleiheDAO();

        try {
            Buch buch = buchDAO.lesen(1);
            System.out.println("Gelesen: " + buch);

            int ausleiheId = ausleiheDAO.ausleihen(1, 1, LocalDate.now(), LocalDate.now().plusWeeks(4));
            System.out.println("Ausgeliehen, Ausleihe-ID: " + ausleiheId);

            ausleiheDAO.zurueckgeben(ausleiheId, 1, LocalDate.now());
            System.out.println("Zurückgegeben.");

        } catch (SQLException | AusleiheDAO.BuchNichtVerfuegbarException e) {
            System.err.println("Fehler: " + e.getMessage());
        }
    }
}
