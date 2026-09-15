-- ============================================================
-- Bibliotheksverwaltung – Datenbankschema
-- M450 LB2 – Integrationstesting
-- ============================================================

CREATE DATABASE IF NOT EXISTS bibliothek
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bibliothek;

-- ------------------------------------------------------------
-- Tabelle: mitglied
-- ------------------------------------------------------------
CREATE TABLE mitglied (
    mitglied_id     INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    ausleihlimit    INT NOT NULL DEFAULT 5,
    CONSTRAINT uq_mitglied_email UNIQUE (email),
    CONSTRAINT chk_ausleihlimit CHECK (ausleihlimit > 0)
);

-- ------------------------------------------------------------
-- Tabelle: buch
-- ------------------------------------------------------------
CREATE TABLE buch (
    buch_id         INT AUTO_INCREMENT PRIMARY KEY,
    titel           VARCHAR(200) NOT NULL,
    autor           VARCHAR(150) NOT NULL,
    isbn            VARCHAR(13) NOT NULL,
    verfuegbar      BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_buch_isbn UNIQUE (isbn)
);

-- ------------------------------------------------------------
-- Tabelle: ausleihe (verknüpft mitglied und buch)
-- ------------------------------------------------------------
CREATE TABLE ausleihe (
    ausleihe_id         INT AUTO_INCREMENT PRIMARY KEY,
    buch_id             INT NOT NULL,
    mitglied_id         INT NOT NULL,
    ausleihdatum        DATE NOT NULL,
    faelligkeitsdatum   DATE NOT NULL,
    rueckgabedatum      DATE NULL,
    CONSTRAINT fk_ausleihe_buch
        FOREIGN KEY (buch_id) REFERENCES buch(buch_id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_ausleihe_mitglied
        FOREIGN KEY (mitglied_id) REFERENCES mitglied(mitglied_id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_faelligkeit CHECK (faelligkeitsdatum > ausleihdatum)
);

-- ------------------------------------------------------------
-- Testdaten
-- ------------------------------------------------------------
INSERT INTO mitglied (name, email, ausleihlimit) VALUES
    ('Anna Meier', 'anna.meier@example.ch', 5),
    ('Ben Suter', 'ben.suter@example.ch', 3);

INSERT INTO buch (titel, autor, isbn, verfuegbar) VALUES
    ('Clean Code', 'Robert C. Martin', '9780132350884', TRUE),
    ('Effective Java', 'Joshua Bloch', '9780134685991', TRUE),
    ('Design Patterns', 'Gamma et al.', '9780201633610', TRUE);

-- Zugang für die Java-Anwendung (für lokale Starts und Docker).
CREATE USER IF NOT EXISTS 'biblio_user'@'localhost' IDENTIFIED BY 'changeme';
CREATE USER IF NOT EXISTS 'biblio_user'@'%' IDENTIFIED BY 'changeme';
GRANT ALL PRIVILEGES ON bibliothek.* TO 'biblio_user'@'localhost';
GRANT ALL PRIVILEGES ON bibliothek.* TO 'biblio_user'@'%';
FLUSH PRIVILEGES;
