-- =============================================================================
-- eParafia — DDL (H2-compatible)
-- Wygenerowane na podstawie encji JPA — czerwiec 2026
-- Kolejność: tabele bez zależności → tabele z kluczami obcymi
-- =============================================================================

-- ── Parish information ────────────────────────────────────────────────────────

CREATE TABLE diocese (
    id     BIGINT       NOT NULL,
    name   VARCHAR(255),
    see    VARCHAR(255),
    bishop VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE locality (
    id          BIGINT       NOT NULL,
    name        VARCHAR(255),
    postal_code VARCHAR(255),
    province    VARCHAR(255),
    diocese_id  BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (diocese_id) REFERENCES diocese(id)
);

CREATE TABLE parish (
    id           BIGINT       NOT NULL,
    name         VARCHAR(255),
    address      VARCHAR(255),
    phone        VARCHAR(255),
    email        VARCHAR(255),
    founded_date DATE,
    locality_id  BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (locality_id) REFERENCES locality(id)
);

-- ── Pastoral care ─────────────────────────────────────────────────────────────

CREATE TABLE family (
    id           BIGINT       NOT NULL,
    family_name  VARCHAR(255),
    member_count INTEGER,
    PRIMARY KEY (id)
);

CREATE TABLE family_address (
    id               BIGINT       NOT NULL,
    street           VARCHAR(255),
    house_number     VARCHAR(255),
    apartment_number VARCHAR(255),
    postal_code      VARCHAR(255),
    city             VARCHAR(255),
    family_id        BIGINT UNIQUE,
    PRIMARY KEY (id),
    FOREIGN KEY (family_id) REFERENCES family(id)
);

CREATE TABLE parishioner (
    id         BIGINT       NOT NULL,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    pesel      VARCHAR(255),
    birth_date DATE,
    phone      VARCHAR(255),
    email      VARCHAR(255),
    parish_id  BIGINT,
    family_id  BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (parish_id) REFERENCES parish(id),
    FOREIGN KEY (family_id) REFERENCES family(id)
);

-- ── Parish information — records & documents ──────────────────────────────────

CREATE TABLE parish_record (
    id             BIGINT       NOT NULL,
    created_date   DATE,
    description    VARCHAR(255),
    parishioner_id BIGINT UNIQUE,
    PRIMARY KEY (id),
    FOREIGN KEY (parishioner_id) REFERENCES parishioner(id)
);

CREATE TABLE document (
    id          BIGINT       NOT NULL,
    type        VARCHAR(255),
    issue_date  DATE,
    description VARCHAR(255),
    record_id   BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (record_id) REFERENCES parish_record(id)
);

-- ── Parish groups ─────────────────────────────────────────────────────────────

CREATE TABLE parish_group (
    id          BIGINT       NOT NULL,
    name        VARCHAR(255),
    description VARCHAR(255),
    supervisor  VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE membership (
    id             BIGINT NOT NULL,
    start_date     DATE,
    end_date       DATE,
    group_id       BIGINT,
    parishioner_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (group_id)       REFERENCES parish_group(id),
    FOREIGN KEY (parishioner_id) REFERENCES parishioner(id)
);

-- ── Staff management ──────────────────────────────────────────────────────────

CREATE TABLE position (
    id          BIGINT       NOT NULL,
    name        VARCHAR(255),
    description VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE employee (
    id          BIGINT       NOT NULL,
    first_name  VARCHAR(255),
    last_name   VARCHAR(255),
    parish_id   BIGINT,
    position_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (parish_id)   REFERENCES parish(id),
    FOREIGN KEY (position_id) REFERENCES position(id)
);

CREATE TABLE duty (
    id          BIGINT       NOT NULL,
    name        VARCHAR(255),
    description VARCHAR(255),
    status      VARCHAR(255),
    position_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (position_id) REFERENCES position(id)
);

-- ── Event coordination ────────────────────────────────────────────────────────

CREATE TABLE event_type (
    id   BIGINT       NOT NULL,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE schedule (
    id          BIGINT       NOT NULL,
    date        DATE,
    time        TIME,
    description VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE parish_event (
    id            BIGINT       NOT NULL,
    name          VARCHAR(255),
    date_time     TIMESTAMP,
    place         VARCHAR(255),
    description   VARCHAR(255),
    parish_id     BIGINT,
    event_type_id BIGINT,
    schedule_id   BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (parish_id)     REFERENCES parish(id),
    FOREIGN KEY (event_type_id) REFERENCES event_type(id),
    FOREIGN KEY (schedule_id)   REFERENCES schedule(id)
);

CREATE TABLE intention (
    id       BIGINT       NOT NULL,
    content  VARCHAR(255),
    date     DATE,
    donor    VARCHAR(255),
    status   VARCHAR(255),
    event_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (event_id) REFERENCES parish_event(id)
);

CREATE TABLE announcement (
    id       BIGINT       NOT NULL,
    content  VARCHAR(255),
    event_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (event_id) REFERENCES parish_event(id)
);

CREATE TABLE offering (
    id       BIGINT         NOT NULL,
    amount   NUMERIC(19, 2),
    date     DATE,
    type     VARCHAR(255),
    event_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (event_id) REFERENCES parish_event(id)
);

CREATE TABLE participant (
    id         BIGINT       NOT NULL,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    role       VARCHAR(255),
    event_id   BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (event_id) REFERENCES parish_event(id)
);

CREATE TABLE organizer (
    id         BIGINT       NOT NULL,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    role       VARCHAR(255),
    event_id   BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (event_id) REFERENCES parish_event(id)
);

-- ── Sacramental ministry ──────────────────────────────────────────────────────

CREATE TABLE sacrament (
    id          BIGINT       NOT NULL,
    name        VARCHAR(255),
    description VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE priest (
    id               BIGINT       NOT NULL,
    first_name       VARCHAR(255),
    last_name        VARCHAR(255),
    phone            VARCHAR(255),
    email            VARCHAR(255),
    ordination_date  DATE,
    role             VARCHAR(255),
    parish_id        BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (parish_id) REFERENCES parish(id)
);

CREATE TABLE sacrament_administration (
    id                  BIGINT NOT NULL,
    administration_date DATE,
    parishioner_id      BIGINT,
    priest_id           BIGINT,
    sacrament_id        BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (parishioner_id) REFERENCES parishioner(id),
    FOREIGN KEY (priest_id)      REFERENCES priest(id),
    FOREIGN KEY (sacrament_id)   REFERENCES sacrament(id)
);
