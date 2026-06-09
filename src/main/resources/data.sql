-- =============================================================================
-- eParafia — dane startowe (seed)
-- Po zmianie tego pliku: zatrzymaj aplikację, usuń folder data/, uruchom ponownie.
-- =============================================================================

-- ── Diocese and locality ────────────────────────────────────────────────────

INSERT INTO diocese (id, name, see, bishop)
VALUES (1, 'Diecezja Rzeszowska', 'Rzeszow', 'Jan Kowalski');

INSERT INTO locality (id, name, postal_code, province, diocese_id)
VALUES (1, 'Rzeszow', '35-001', 'podkarpackie', 1);

-- ── Parish ───────────────────────────────────────────────────────────────────

INSERT INTO parish (id, name, address, phone, email, founded_date, locality_id)
VALUES (1, 'Parafia sw. Jana Pawla II', 'ul. Koscielna 1', '123456789', 'kontakt@parafia.pl', '1999-05-15', 1);

-- ── Parish staff (positions, employees, duties) ──────────────────────────────

INSERT INTO position (id, name, description)
VALUES
(1, 'Koscielny', 'Utrzymanie porzadku w kosciele i dzwonienie'),
(2, 'Organista', 'Oprawa muzyczna mszy i uroczystosci');

INSERT INTO employee (id, first_name, last_name, parish_id, position_id)
VALUES (1, 'Piotr', 'Zielinski', 1, 1);

INSERT INTO duty (id, name, description, status, position_id)
VALUES
(1, 'Dzwonienie przed msza', 'Dzwonek 10 minut przed rozpoczeciem', 'ASSIGNED', 1),
(2, 'Sprzatanie naw', 'Porzadek po niedzielnej mszy', 'COMPLETED', 1);

-- ── Parish events ─────────────────────────────────────────────────────────────

INSERT INTO event_type (id, name)
VALUES
(1, 'Msza Swieta'),
(2, 'Rekolekcje'),
(3, 'Spotkanie grupy');

INSERT INTO schedule (id, date, time, description)
VALUES
(1, '2026-05-30', '18:00:00', 'Msza wieczorna'),
(2, '2026-06-07', '10:00:00', 'Msza niedzielna poranna');

INSERT INTO parish_event (id, name, date_time, place, description, parish_id, event_type_id, schedule_id)
VALUES
(1, 'Msza niedzielna', '2026-05-30T18:00:00', 'Kosciol glowny', 'Zwykla msza', 1, 1, 1),
(2, 'Rekolekcje wielkopostne', '2026-06-07T10:00:00', 'Kaplica boczna', 'Dzien 3 rekolekcji', 1, 2, 2);

-- Intentions — event 1: PLANNED + REALIZED; event 2: only PLANNED (test eventId/status filter)
INSERT INTO intention (id, content, date, donor, status, event_id)
VALUES
(1, 'Za zdrowie rodziny Kowalskich', '2026-05-30', 'Jan Kowalski', 'PLANNED', 1),
(2, 'W intencji zmarlych', '2026-05-30', 'Anna Nowak', 'REALIZED', 1),
(3, 'Za misje zagraniczne', '2026-06-07', 'Parafia sw. Jana Pawla II', 'PLANNED', 2);

-- Announcements — event 1 (2 pcs) and event 2 (1 pc) — test GET /api/announcements?eventId=1
INSERT INTO announcement (id, content, event_id)
VALUES
(1, 'Zbiorka na remont dachu odbedzie sie po mszy.', 1),
(2, 'Spotkanie rady parafialnej w piatek o 17:00.', 1),
(3, 'Rekolekcje trwaja do niedzieli — zapraszamy codziennie o 18:00.', 2);

-- Offerings — event 1 and 2 (test eventId, type filters)
INSERT INTO offering (id, amount, date, type, event_id)
VALUES
(1, 150.00, '2026-05-30', 'NA_TACE', 1),
(2, 500.00, '2026-05-30', 'INTENCJA', 1),
(3, 200.00, '2026-06-07', 'NA_TACE', 2);

-- Participants and organizers
INSERT INTO participant (id, first_name, last_name, role, event_id)
VALUES
(1, 'Tomasz', 'Wojcik', 'Ministrant', 1),
(2, 'Kasia', 'Lis', 'Lektor', 1),
(3, 'Marek', 'Sadowski', 'Ministrant', 2);

INSERT INTO organizer (id, first_name, last_name, role, event_id)
VALUES
(1, 'Michal', 'Nowak', 'Koordynator liturgii', 1),
(2, 'Ewa', 'Kaminska', 'Koordynatorka rekolekcji', 2);

-- ── Parish community ──────────────────────────────────────────────────────────

INSERT INTO family (id, family_name, member_count)
VALUES
(1, 'Kowalscy', 3),
(2, 'Nowakowie', 4);

INSERT INTO family_address (id, street, house_number, apartment_number, postal_code, city, family_id)
VALUES (1, 'ul. Lipowa', '12', '4', '35-001', 'Rzeszow', 1);

INSERT INTO parishioner (id, first_name, last_name, pesel, birth_date, phone, email, parish_id, family_id)
VALUES
(1, 'Jan', 'Kowalski', '90010112345', '1990-01-01', '600700800', 'jan.kowalski@example.com', 1, 1),
(2, 'Anna', 'Nowak', '92050567890', '1992-05-05', '601702803', 'anna.nowak@example.com', 1, 2),
(3, 'Piotr', 'Wisniewski', '88031211111', '1988-03-12', '602803904', 'piotr.wisniewski@example.com', 1, NULL);

-- Parish record and documents for parishioner 1 (test GET /api/documents?recordId=1)
INSERT INTO parish_record (id, created_date, description, parishioner_id)
VALUES (1, '2020-01-15', 'Kartoteka Jan Kowalski — chrzest, bierzmowanie', 1);

INSERT INTO document (id, type, issue_date, description, record_id)
VALUES
(1, 'Chrzest', '1990-06-10', 'Swiadectwo chrztu — kosciol parafialny Rzeszow', 1),
(2, 'Bierzmowanie', '2005-05-20', 'Swiadectwo bierzmowania', 1);

-- ── Parish groups and memberships ────────────────────────────────────────────

INSERT INTO parish_group (id, name, description, supervisor)
VALUES
(1, 'Ministranci', 'Sluzba liturgiczna oltarza', 'Michal Nowak'),
(2, 'Schola', 'Oprawa muzyczna, spiew mlodziezowy', 'Anna Wisniewska');

INSERT INTO membership (id, start_date, end_date, group_id, parishioner_id)
VALUES
(1, '2024-09-01', NULL, 1, 1),
(2, '2023-01-01', '2025-12-31', 2, 2);

-- ── Sacramental ministry ─────────────────────────────────────────────────────

INSERT INTO sacrament (id, name, description)
VALUES
(1, 'Chrzest', 'Przyjecie do wspolnoty Kosciola'),
(2, 'Bierzmowanie', 'Umacnianie w wierze'),
(3, 'Eucharystia', 'Komunia swieta'),
(4, 'Pokuta', 'Spowiedz swieta'),
(5, 'Malzenstwo', 'Zwiazek malzenski'),
(6, 'Kaplanstwo', 'Swiecenia kaplanskie'),
(7, 'Namaszczenie chorych', 'Sakrament chorych');

INSERT INTO priest (id, first_name, last_name, phone, email, ordination_date, role, parish_id)
VALUES
(1, 'Adam', 'Nowak', '111222333', 'adam.nowak@parafia.pl', '2010-06-15', 'Proboszcz', 1),
(2, 'Tomasz', 'Kowalczyk', '444555666', 'tomasz.kowalczyk@parafia.pl', '2015-06-20', 'Wikariusz', 1);

-- Sacrament administration (test GET /api/sacrament-administrations?parishionerId=1)
INSERT INTO sacrament_administration (id, administration_date, parishioner_id, priest_id, sacrament_id)
VALUES (1, '2025-05-25', 1, 1, 1);
