package ch.m450.bibliothek.model;

public class Buch {
    private int buchId;
    private String titel;
    private String autor;
    private String isbn;
    private boolean verfuegbar;

    public Buch(int buchId, String titel, String autor, String isbn, boolean verfuegbar) {
        this.buchId = buchId;
        this.titel = titel;
        this.autor = autor;
        this.isbn = isbn;
        this.verfuegbar = verfuegbar;
    }

    public int getBuchId() { return buchId; }
    public String getTitel() { return titel; }
    public String getAutor() { return autor; }
    public String getIsbn() { return isbn; }
    public boolean isVerfuegbar() { return verfuegbar; }

    @Override
    public String toString() {
        return "Buch{id=" + buchId + ", titel='" + titel + "', autor='" + autor
                + "', isbn='" + isbn + "', verfuegbar=" + verfuegbar + "}";
    }
}
