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
