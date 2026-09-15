package ch.m450.bibliothek.model;

import java.time.LocalDate;

public class Ausleihe {
    private int ausleiheId;
    private int buchId;
    private int mitgliedId;
    private LocalDate ausleihdatum;
    private LocalDate faelligkeitsdatum;
    private LocalDate rueckgabedatum; // null solange nicht zurückgegeben

    public Ausleihe(int ausleiheId, int buchId, int mitgliedId,
                     LocalDate ausleihdatum, LocalDate faelligkeitsdatum, LocalDate rueckgabedatum) {
        this.ausleiheId = ausleiheId;
        this.buchId = buchId;
        this.mitgliedId = mitgliedId;
        this.ausleihdatum = ausleihdatum;
        this.faelligkeitsdatum = faelligkeitsdatum;
        this.rueckgabedatum = rueckgabedatum;
    }

    public int getAusleiheId() { return ausleiheId; }
    public int getBuchId() { return buchId; }
    public int getMitgliedId() { return mitgliedId; }
    public LocalDate getAusleihdatum() { return ausleihdatum; }
    public LocalDate getFaelligkeitsdatum() { return faelligkeitsdatum; }
    public LocalDate getRueckgabedatum() { return rueckgabedatum; }

    @Override
    public String toString() {
        return "Ausleihe{id=" + ausleiheId + ", buchId=" + buchId + ", mitgliedId=" + mitgliedId
                + ", ausleihdatum=" + ausleihdatum + ", faelligkeitsdatum=" + faelligkeitsdatum
                + ", rueckgabedatum=" + rueckgabedatum + "}";
    }
}
