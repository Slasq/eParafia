-- =============================================================================
-- eParafia — dane startowe (seed)
-- Po zmianie tego pliku: zatrzymaj aplikację, usuń folder data/, uruchom ponownie.
-- =============================================================================

-- ── Diecezja i miejscowość ───────────────────────────────────────────────────

INSERT INTO diecezja (id, nazwa, siedziba, biskup)
VALUES (1, 'Diecezja Rzeszowska', 'Rzeszow', 'Jan Kowalski');

INSERT INTO miejscowosc (id, nazwa, kod_pocztowy, wojewodztwo, diecezja_id)
VALUES (1, 'Rzeszow', '35-001', 'podkarpackie', 1);

-- ── Parafia ────────────────────────────────────────────────────────────────────

INSERT INTO parafia (id, nazwa, adres, telefon, email, data_erygowania, miejscowosc_id)
VALUES (1, 'Parafia sw. Jana Pawla II', 'ul. Koscielna 1', '123456789', 'kontakt@parafia.pl', '1999-05-15', 1);

-- ── Personel parafii (stanowiska, pracownik, obowiązki) ─────────────────────

INSERT INTO stanowisko (id, nazwa, opis)
VALUES
(1, 'Koscielny', 'Utrzymanie porzadku w kosciele i dzwonienie'),
(2, 'Organista', 'Oprawa muzyczna mszy i uroczystosci');

INSERT INTO pracownik (id, imie, nazwisko, parafia_id, stanowisko_id)
VALUES (1, 'Piotr', 'Zielinski', 1, 1);

INSERT INTO obowiazek (id, nazwa, opis, status, stanowisko_id)
VALUES
(1, 'Dzwonienie przed msza', 'Dzwonek 10 minut przed rozpoczeciem', 'ASSIGNED', 1),
(2, 'Sprzatanie naw', 'Porzadek po niedzielnej mszy', 'COMPLETED', 1);

-- ── Wydarzenia parafialne ──────────────────────────────────────────────────────

INSERT INTO typ_wydarzenia (id, nazwa)
VALUES
(1, 'Msza Swieta'),
(2, 'Rekolekcje'),
(3, 'Spotkanie grupy');

INSERT INTO harmonogram (id, data, godzina, opis)
VALUES
(1, '2026-05-30', '18:00:00', 'Msza wieczorna'),
(2, '2026-06-07', '10:00:00', 'Msza niedzielna poranna');

INSERT INTO wydarzenie_parafialne (id, nazwa, data_i_godzina, miejsce, opis, parafia_id, typ_wydarzenia_id, harmonogram_id)
VALUES
(1, 'Msza niedzielna', '2026-05-30T18:00:00', 'Kosciol glowny', 'Zwykla msza', 1, 1, 1),
(2, 'Rekolekcje wielkopostne', '2026-06-07T10:00:00', 'Kaplica boczna', 'Dzien 3 rekolekcji', 1, 2, 2);

-- Intencje — event 1: PLANNED + REALIZED; event 2: tylko PLANNED (test filtrowania eventId/status)
INSERT INTO intencja (id, tresc, data, ofiarodawca, status, wydarzenie_id)
VALUES
(1, 'Za zdrowie rodziny Kowalskich', '2026-05-30', 'Jan Kowalski', 'PLANNED', 1),
(2, 'W intencji zmarlych', '2026-05-30', 'Anna Nowak', 'REALIZED', 1),
(3, 'Za misje zagraniczne', '2026-06-07', 'Parafia sw. Jana Pawla II', 'PLANNED', 2);

-- Ogłoszenia — event 1 (2 szt.) i event 2 (1 szt.) — test GET /api/announcements?eventId=1
INSERT INTO ogloszenie (id, tresc, wydarzenie_id)
VALUES
(1, 'Zbiorka na remont dachu odbedzie sie po mszy.', 1),
(2, 'Spotkanie rady parafialnej w piatek o 17:00.', 1),
(3, 'Rekolekcje trwaja do niedzieli — zapraszamy codziennie o 18:00.', 2);

-- Ofiary — event 1 i 2 (test filtrowania eventId, type)
INSERT INTO ofiara (id, kwota, data, typ, wydarzenie_id)
VALUES
(1, 150.00, '2026-05-30', 'NA_TACE', 1),
(2, 500.00, '2026-05-30', 'INTENCJA', 1),
(3, 200.00, '2026-06-07', 'NA_TACE', 2);

-- Uczestnicy i organizatorzy
INSERT INTO uczestnik (id, imie, nazwisko, rola, wydarzenie_id)
VALUES
(1, 'Tomasz', 'Wojcik', 'Ministrant', 1),
(2, 'Kasia', 'Lis', 'Lektor', 1),
(3, 'Marek', 'Sadowski', 'Ministrant', 2);

INSERT INTO organizator (id, imie, nazwisko, rola, wydarzenie_id)
VALUES
(1, 'Michal', 'Nowak', 'Koordynator liturgii', 1),
(2, 'Ewa', 'Kaminska', 'Koordynatorka rekolekcji', 2);

-- ── Wspólnota parafialna ───────────────────────────────────────────────────────

INSERT INTO rodzina (id, nazwisko_rodziny, liczba_czlonkow)
VALUES
(1, 'Kowalscy', 3),
(2, 'Nowakowie', 4);

INSERT INTO adres_rodziny (id, ulica, numer_domu, numer_mieszkania, kod_pocztowy, miasto, rodzina_id)
VALUES (1, 'ul. Lipowa', '12', '4', '35-001', 'Rzeszow', 1);

INSERT INTO parafianin (id, imie, nazwisko, pesel, data_urodzenia, telefon, email, parafia_id, rodzina_id)
VALUES
(1, 'Jan', 'Kowalski', '90010112345', '1990-01-01', '600700800', 'jan.kowalski@example.com', 1, 1),
(2, 'Anna', 'Nowak', '92050567890', '1992-05-05', '601702803', 'anna.nowak@example.com', 1, 2),
(3, 'Piotr', 'Wisniewski', '88031211111', '1988-03-12', '602803904', 'piotr.wisniewski@example.com', 1, NULL);

-- Kartoteka i dokumenty parafianina 1 (test GET /api/documents?recordId=1)
INSERT INTO kartoteka (id, data_utworzenia, opis, parafianin_id)
VALUES (1, '2020-01-15', 'Kartoteka Jan Kowalski — chrzest, bierzmowanie', 1);

INSERT INTO dokument (id, typ, data_wystawienia, opis, kartoteka_id)
VALUES
(1, 'Chrzest', '1990-06-10', 'Swiadectwo chrztu — kosciol parafialny Rzeszow', 1),
(2, 'Bierzmowanie', '2005-05-20', 'Swiadectwo bierzmowania', 1);

-- ── Grupy parafialne i członkostwa ───────────────────────────────────────────

INSERT INTO grupa_parafialna (id, nazwa, opis, opiekun)
VALUES
(1, 'Ministranci', 'Sluzba liturgiczna oltarza', 'Michal Nowak'),
(2, 'Schola', 'Oprawa muzyczna, spiew mlodziezowy', 'Anna Wisniewska');

INSERT INTO czlonkostwo (id, data_od_kiedy, data_do_kiedy, grupa_id, parafianin_id)
VALUES
(1, '2024-09-01', NULL, 1, 1),
(2, '2023-01-01', '2025-12-31', 2, 2);

-- ── Posługa sakramentalna ────────────────────────────────────────────────────

INSERT INTO sakrament (id, nazwa, opis)
VALUES
(1, 'Chrzest', 'Przyjecie do wspolnoty Kosciola'),
(2, 'Bierzmowanie', 'Umacnianie w wierze'),
(3, 'Eucharystia', 'Komunia swieta'),
(4, 'Pokuta', 'Spowiedz swieta'),
(5, 'Malzenstwo', 'Zwiazek malzenski'),
(6, 'Kaplanstwo', 'Swiecenia kaplanskie'),
(7, 'Namaszczenie chorych', 'Sakrament chorych');

INSERT INTO ksiadz (id, imie, nazwisko, telefon, email, data_swiecen, funkcja, parafia_id)
VALUES
(1, 'Adam', 'Nowak', '111222333', 'adam.nowak@parafia.pl', '2010-06-15', 'Proboszcz', 1),
(2, 'Tomasz', 'Kowalczyk', '444555666', 'tomasz.kowalczyk@parafia.pl', '2015-06-20', 'Wikariusz', 1);

-- Rejestracja sakramentu (test GET /api/sacrament-administrations?parishionerId=1)
INSERT INTO udzielanie_sakramentu (id, data_udzielenia, parafianin_id, ksiadz_id, sakrament_id)
VALUES (1, '2025-05-25', 1, 1, 1);
