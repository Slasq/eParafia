INSERT INTO Diecezja (id_diecezji, nazwa, siedziba, biskup)
VALUES (1, 'Diecezja Rzeszowska', 'Rzeszów', 'Jan Kowalski');

INSERT INTO Sakrament (id_sakramentu, nazwa, opis)
VALUES
(1, 'Chrzest', 'Włączenie do wspólnoty Kościoła'),
(2, 'Bierzmowanie', 'Przyjęcie darów Ducha Świętego'),
(3, 'Małżeństwo', 'Sakrament związku małżeńskiego');

INSERT INTO Typ_wydarzenia (id_typu_wydarzenia, nazwa)
VALUES
(1, 'Msza Święta'),
(2, 'Rekolekcje'),
(3, 'Spotkanie grupy');

INSERT INTO Stanowisko (id_stanowiska, nazwa, opis)
VALUES
(1, 'Koscielny', 'Utrzymanie porządku w kościele i dzwonienie'),
(2, 'Organista', 'Oprawa muzyczna mszy i uroczystości');

INSERT INTO Grupa (id_grupy, nazwa, opis, opiekun)
VALUES
(1, 'Ministranci', 'Służba liturgiczna ołtarza', 'Michał Nowak'),
(2, 'Schola', 'Oprawa muzyczna, śpiew młodzieżowy', 'Anna Wiśniewska');

INSERT INTO Parafia (id_parafii, nazwa, adres, telefon, email, data_erygowania)
VALUES (1, 'Parafia sw. Jana Pawla II', 'ul. Koscielna 1', '123456789', 'kontakt@parafia.pl', '1999-05-15');

INSERT INTO Miejscowosc (id_miejscowosci, nazwa, kod_pocztowy, wojewodztwo, Diecezja_id_diecezji, Parafia_id_parafii)
VALUES (1, 'Rzeszow', '35-001', 'Podkarpackie', 1, 1);

INSERT INTO Ksiadz (id_ksiedza, imie, nazwisko, data_swiecen, telefon, email, funkcja, Parafia_id_parafii)
VALUES 
(1, 'Adam', 'Kowalski', '2010-05-20', '987654321', 'adam.kowalski@parafia.pl', 'Proboszcz', 1),
(2, 'Piotr', 'Nowak', '2018-05-25', '111222333', 'piotr.nowak@parafia.pl', 'Wikariusz', 1);

INSERT INTO Pracownik (imie, nazwisko, Parafia_id_parafii, Stanowisko_id_stanowiska)
VALUES ('Marek', 'Zalewski', 1, 1);

INSERT INTO Rodzina (id_rodziny, nazwisko_rodziny, liczba_czlonkow)
VALUES (1, 'Wisniewscy', 4);

INSERT INTO Adres_rodziny (id_adresu, ulica, nr_domu, nr_mieszkania, kod_pocztowy, miasto, Rodzina_id_rodziny)
VALUES (1, 'Kwiatowa', '15', '2', '35-111', 'Rzeszow', 1);

INSERT INTO Parafianin (id_parafianina, imie, nazwisko, pesel, data_urodzenia, telefon, email, Parafia_id_parafii, Rodzina_id_rodziny)
VALUES 
(1, 'Jan', 'Wisniewski', 80010112345, '1980-01-01', '555666777', 'jan.w@mail.com', 1, 1),
(2, 'Anna', 'Wisniewska', 82020254321, '1982-02-02', '777888999', 'anna.w@mail.com', 1, 1);


INSERT INTO Harmonogram (id_harmonogramu, data, godzina, opis)
VALUES (1, '2026-05-30', '2026-05-30', 'Msza wieczorna');

INSERT INTO Wydarzenie (id_wydarzenia, nazwa, data, miejsce, opis, Parafia_id_parafii, Typ_wydarzenia_id_typu_wydarzenia, Harmonogram_id_harmonogramu)
VALUES (1, 'Msza niedzielna', '2026-05-30', 'Kosciol glowny', 'Zwykla msza', 1, 1, 1);

INSERT INTO Intencja (id_intencji, tresc, data, ofiarodawca, Wydarzenie_id_wydarzenia)
VALUES (1, 'O zdrowie i blogoslawienstwo', '2026-05-30', 'Jan Wisniewski', 1);

INSERT INTO Ofiara (id_ofiary, kwota, data, typ, Wydarzenie_id_wydarzenia)
VALUES (1, 50.0, '2026-05-30', 'Taca', 1);

INSERT INTO Czlonkostwo (data_od_kiedy, data_do_kiedy, Parafianin_id_parafianina, Grupa_id_grupy)
VALUES ('2025-01-01', NULL, 1, 2);

INSERT INTO Udzielanie_sakramentu (data, parafianin_id, ksiadz_id, sakrament_id)
VALUES ('2026-05-23', 1, 2, 5);