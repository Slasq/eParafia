INSERT INTO diecezja (id, nazwa, siedziba, biskup)
VALUES (1, 'Diecezja Rzeszowska', 'Rzeszow', 'Jan Kowalski');

INSERT INTO parafia (id, nazwa, adres, telefon, email, data_erygowania)
VALUES (1, 'Parafia sw. Jana Pawla II', 'ul. Koscielna 1', '123456789', 'kontakt@parafia.pl', '1999-05-15');

INSERT INTO stanowisko (id, nazwa, opis)
VALUES
(1, 'Koscielny', 'Utrzymanie porzadku w kosciele i dzwonienie'),
(2, 'Organista', 'Oprawa muzyczna mszy i uroczystosci');

INSERT INTO typ_wydarzenia (id, nazwa)
VALUES
(1, 'Msza Swieta'),
(2, 'Rekolekcje'),
(3, 'Spotkanie grupy');

INSERT INTO harmonogram (id, data, godzina, opis)
VALUES (1, '2026-05-30', '18:00:00', 'Msza wieczorna');

INSERT INTO wydarzenie_parafialne (id, nazwa, data_i_godzina, miejsce, opis, parafia_id, typ_wydarzenia_id, harmonogram_id)
VALUES (1, 'Msza niedzielna', '2026-05-30T18:00:00', 'Kosciol glowny', 'Zwykla msza', 1, 1, 1);

INSERT INTO grupa_parafialna (id, nazwa, opis, opiekun)
VALUES
(1, 'Ministranci', 'Sluzba liturgiczna oltarza', 'Michal Nowak'),
(2, 'Schola', 'Oprawa muzyczna, spiew mlodziezowy', 'Anna Wisniewska');

INSERT INTO sakrament (id, nazwa, opis)
VALUES
(1, 'Chrzest', 'Przyjecie do wspolnoty Kosciola'),
(2, 'Bierzmowanie', 'Umacnianie w wierze'),
(3, 'Malzenstwo', 'Zwiazek malzenski');

INSERT INTO ksiadz (id, imie, nazwisko, telefon, email, data_swiecen, funkcja, parafia_id)
VALUES (1, 'Adam', 'Nowak', '111222333', 'adam.nowak@parafia.pl', '2010-06-15', 'Proboszcz', 1);

INSERT INTO rodzina (id, nazwisko_rodziny, liczba_czlonkow)
VALUES (1, 'Kowalscy', 3);

INSERT INTO parafianin (id, imie, nazwisko, pesel, data_urodzenia, telefon, email, parafia_id, rodzina_id)
VALUES (1, 'Jan', 'Kowalski', '90010112345', '1990-01-01', '600700800', 'jan.kowalski@example.com', 1, 1);
