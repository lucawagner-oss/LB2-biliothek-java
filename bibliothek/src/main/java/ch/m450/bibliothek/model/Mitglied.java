package ch.m450.bibliothek.model;

public class Mitglied {
    private int mitgliedId;
    private String name;
    private String email;
    private int ausleihlimit;

    public Mitglied(int mitgliedId, String name, String email, int ausleihlimit) {
        this.mitgliedId = mitgliedId;
        this.name = name;
        this.email = email;
        this.ausleihlimit = ausleihlimit;
    }

    public int getMitgliedId() { return mitgliedId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getAusleihlimit() { return ausleihlimit; }

    @Override
    public String toString() {
        return "Mitglied{id=" + mitgliedId + ", name='" + name + "', email='" + email
                + "', ausleihlimit=" + ausleihlimit + "}";
    }
}
