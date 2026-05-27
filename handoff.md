# eParafia — przekazanie projektu (handoff)

Dokument dla zespołu przejmującego dalszą pracę. Ostatnia aktualizacja stanu: **maj 2026**.

| | |
|---|---|
| **Repozytorium** | https://github.com/Slasq/eParafia |
| **Prezentacja** | 16–17.06.2026 |
| **Kurs** | Usługi sieciowe w biznesie (PRz, IAD L2) |
| **Instrukcja uruchomienia** | [`README.md`](README.md) |
| **Dokumentacja analityczna** | [`docs/docs.md`](docs/docs.md) |
| **Wytyczne testera** | [`docs/wytyczne_do_testów.md`](docs/wytyczne_do_testów.md) |

---

## 1. Co to jest

Backend **REST** do zarządzania parafią: wierni, wydarzenia, grupy, personel, sakramenty itd.  
**Front nie jest wymagany** — wystarczą API + Bruno + H2 Console na demo.

Stack: **Java 26**, **Spring Boot 4.0.6**, **JPA/Hibernate 7**, **H2** (plik), **Lombok**, **Gradle 9.4**, **springdoc-openapi 2.8.9**.

---

## 2. Szybki start

```powershell
cd ścieżka\do\eParafia
.\gradlew.bat bootRun
```

| URL | Opis |
|-----|------|
| http://localhost:8080/ | JSON z listą wszystkich use-cases |
| http://localhost:8080/swagger-ui/index.html | Interaktywna dokumentacja API |
| http://localhost:8080/h2-console | Konsola H2 — JDBC `jdbc:h2:file:./data/eparish`, user `sa`, hasło puste |

**Testy automatyczne:**

```powershell
.\gradlew.bat test
```

**Bruno:** Open Collection → folder [`tests/`](tests/).

### Ważne: świeża baza

Jeśli testy Bruno lub JUnit zwracają **404**:

1. Zatrzymaj aplikację.
2. Usuń folder **`data/`** w katalogu projektu.
3. Uruchom ponownie — `data.sql` załaduje pełny seed (diecezja, parafia, harmonogram, **wydarzenie id=1**, grupy, sakramenty, ksiądz, rodzina, parafianin).

W Bruno najpierw odpal **`Event_Coordination/List Events`** — sprawdza, czy seed jest OK.

---

## 3. Architektura

Projekt stosuje **Domain-Driven Design (DDD)** z czystym podziałem warstw:

```
HTTP Request
    ↓
Controller        ← tylko mapowanie request/response, zero logiki
    ↓
Service           ← przypadki użycia, walidacja, orkiestracja
    ↓
Factory           ← tworzenie obiektów domenowych (new + EntityIds.nextId)
    ↓
Repository (JPA)  ← persystencja
```

### Konteksty domenowe

| Pakiet | Serwis | Fabryka | Agregat |
|--------|--------|---------|---------|
| `koordynacjawydarzen` | `EventCoordinationService` | `EventFactory` | `WydarzenieAgregat` *(złożony)* |
| `informacjeoparafii` | `ParishInformationService` | `ParishInfoFactory` | `ParafianinAgregat` |
| `duszpasterstwowiernych` | `PastoralCareService` | `PastoralCareFactory` | — |
| `grupyparafialne` | `ParishGroupService` | `ParishGroupFactory` | `GrupaParafialnaAgregat` |
| `organizacjarolizadan` | `ParishOperationsService` | `StaffFactory` | — |
| `poslugasakramentalna` | `SacramentalMinistryService` | `SacramentalMinistryFactory` | — |

### Agregaty domenowe

| Agregat | Encje składowe | Endpoint |
|---------|---------------|----------|
| `WydarzenieAgregat` *(złożony)* | WydarzenieParafialne + Intencja + Ogloszenie + Ofiara + Uczestnik + Organizator | `GET /api/events/{id}/aggregate` |
| `ParafianinAgregat` | Parafianin + Kartoteka + Dokument | `GET /api/parishioners/{id}/aggregate` |
| `GrupaParafialnaAgregat` | GrupaParafialna + Czlonkostwo | `GET /api/groups/{id}/aggregate` |

### Reguły architektury (ważne!)

- **Fabryki** (`*Factory.java`) — tylko tworzą obiekty domenowe (`new` + `setId` przez `EntityIds.nextId()`), **nigdy nie zapisują** do bazy.
- **Serwisy** (`*Service.java`) — wywołują fabrykę, następnie `repo.save()`, zawierają całą logikę biznesową.
- **Kontrolery** (`api/*.java`) — cienkie delegaty: przyjmują request record → wywołują metodę serwisu → zwracają response record. **Żadnej logiki**.
- **Agregaty** (`domain/*/Agregat.java`) — POJO (nie encje JPA), budowane w serwisach z wielu repozytoriów; zawierają obliczenia domenowe.
- **EntityIds** (`api/support/EntityIds.java`) — `nextId(repo, getter)` zwraca `max(id) + 1`. Wywoływany wyłącznie z fabryk.

---

## 4. Co jest już zrobione

### Backend — komplet

| Obszar | Status |
|--------|--------|
| 24 encje JPA w pakietach DDD | ✅ |
| Repozytoria Spring Data (JpaRepository) | ✅ |
| 6 klas serwisów (`application/`) | ✅ |
| 6 klas fabryk (`application/`) | ✅ |
| 6 kontrolerów REST w `api/` | ✅ |
| 3 agregaty domenowe z metodami | ✅ |
| 20 przypadków użycia zaimplementowanych | ✅ |
| PUT / DELETE na kluczowych zasobach | ✅ |
| Swagger UI (`/swagger-ui/index.html`) | ✅ działający na Java 26 |
| `OpenApiConfig` z pełną dokumentacją UC | ✅ |
| `data.sql` — pełny seed | ✅ |
| `EntityIds` — helper generowania ID | ✅ |
| Pole `status` na `Obowiazek` (ASSIGNED/COMPLETED) | ✅ |
| Pole `status` na `Intencja` (PLANNED/REALIZED) | ✅ |

### Serwisy i przypadki użycia

| Serwis | UC (metody) |
|--------|------------|
| `EventCoordinationService` | createEvent, updateEvent, deleteEvent, assignIntention, **realizeIntention**, addAnnouncement, recordOffering, assignParticipant, assignOrganizer, getEventAggregate |
| `ParishInformationService` | addDiocese, updateDiocese, addLocality, addParish, updateParish, registerParishioner, updateParishioner, deleteParishioner, createRecord, updateRecord, addDocument, getParishionerAggregate |
| `PastoralCareService` | addFamily, updateFamilyName, assignParishionerToFamily, addFamilyAddress, updateFamilyAddress |
| `ParishGroupService` | createGroup, updateGroup, addMembership, terminateMembership, getGroupAggregate |
| `ParishOperationsService` | addEmployee, addPosition, assignDuty, **completeDuty** |
| `SacramentalMinistryService` | addPriest, addSacrament, registerSacrament |

### Metody domenowe agregatów

**WydarzenieAgregat** (złożony):
- `totalOfferings()` — suma złożonych ofiar
- `realizedIntentionCount()` / `plannedIntentionCount()` — intencje wg statusu
- `totalEngaged()` — uczestnicy + organizatorzy łącznie
- `announcementCount()` — liczba ogłoszeń
- `isIntentionAlreadyAssigned(String)` / `hasOrganizer()` — walidacja

**ParafianinAgregat**:
- `hasRecord()`, `documentCount()`, `hasDocumentOfType(String)`, `documentsOfType(String)`, `isProfileComplete()`

**GrupaParafialnaAgregat**:
- `totalMemberCount()`, `activeMembers()`, `activeMembersAt(LocalDate)`, `isMember(Long)`, `formerMembers()`

### Testy

| Artefakt | Status |
|----------|--------|
| `EparishApplicationTests` | ✅ contextLoads |
| `ApiIntegrationTest` | ✅ 9 scenariuszy |
| Profil testowy `application-test.properties` | ✅ H2 in-memory, create-drop |
| Kolekcja Bruno w `tests/` | ✅ 8 requestów |

### Dokumentacja

| Plik | Status |
|------|--------|
| `README.md` | ✅ pełna (architektura, API, JSON przykłady) |
| `docs/docs.md` §1–2 | ✅ |
| `docs/docs.md` §3–5 | ❌ do uzupełnienia przez zespół |
| `handoff.md` | ✅ ten plik |

---

## 5. Dane startowe (`data.sql`)

Po świeżym starcie dostępne:

| ID | Zasób |
|----|-------|
| 1 | diecezja, parafia, stanowiska (1–2), typy wydarzeń, harmonogram, **wydarzenie**, grupy (1–2), sakramenty (1–7), ksiądz, rodzina, parafianin |

Kluczowe zależności w testach:
- **Add Intention / Add Announcement** → wymagają `eventId: 1`
- **Register Sacrament** → `parishionerId: 1`, `priestId: 1`, `sacramentId: 1`
- **Add Employee** → `parishId: 1`, `positionId: 1`

---

## 6. Kolekcja Bruno (`tests/`)

| Folder | Request | Metoda |
|--------|---------|--------|
| Event_Coordination | List Events | GET — **uruchamiać pierwszy** |
| Event_Coordination | Add Intention | POST |
| Event_Coordination | Add Announcement | POST |
| Pastoral_Care | Add Family | POST |
| Parish_Groups | Create Group | POST |
| Staff_Management | Add Employee | POST |
| Sacramental_Ministry | Register Sacrament | POST |
| Parish_Information | List Parishes | GET |

Brakuje requestów Bruno dla wielu nowych endpointów (offerings, participants, organizers, duties, aggregate GET-y itd.) — do dopisania przez testera.

---

## 7. Backlog (co zostało do zrobienia)

### Wymagane na ocenę / prezentację

- [ ] **§3 Projekt** w `docs/docs.md` (diagramy DDD, ERD implementacyjny)
- [ ] **§4 Implementacja** (opis architektury, kontrolery, baza, serwisy, fabryki)
- [ ] **§5 Testy** (raport Bruno + JUnit, zrzuty ekranu)
- [ ] **§6.1 Role** (kto co robił)
- [ ] **`schema.sql`** — DDL eksportowany z H2 po uruchomieniu (`SCRIPT TO 'schema.sql'`)
- [ ] Rozszerzyć **Bruno** o brakujące endpointy (patrz §6)
- [ ] **Suchy przebieg prezentacji** (świeża baza, kolejność requestów, Swagger UI)

### Opcjonalne / jakość

- [ ] Walidacja `@Valid` + `@NotBlank` / `@NotNull` na request DTO
- [ ] Lepsze generowanie ID (sekwencje JPA zamiast `EntityIds.nextId()`)
- [ ] Front (HTML / Thymeleaf) — tylko jeśli prowadzący wymaga
- [ ] Usunąć `docs/jdk-26_windows-x64_bin.exe` z repo (duży plik binarny)

### Znane ograniczenia techniczne

- ID generowane przez `EntityIds.nextId(repo, getter)` — `max(id)+1`. Przy równoległych requestach teoretycznie race condition — nieistotne dla demo i testów.
- Baza plikowa H2 — przy `ddl-auto=update` + `data.sql` z `continue-on-error=true` stary plik `data/` może mieć niepełny seed → wystarczy usunąć `data/` i zrestartować.
- Brak paginacji na listach GET — nieistotne dla rozmiaru danych w projekcie.

---

## 8. Struktura kodu

```
src/main/java/edu/prz/eparish/
├── EparishApplication.java
├── api/                                  # Kontrolery REST (cienkie delegaty)
│   ├── HomeController.java
│   ├── EventCoordinationController.java
│   ├── ParishInformationController.java
│   ├── PastoralCareController.java
│   ├── ParishOperationsController.java
│   ├── SacramentalMinistryController.java
│   └── support/
│       ├── EntityIds.java
│       └── OpenApiConfig.java
├── koordynacjawydarzen/
│   ├── application/
│   │   ├── EventFactory.java
│   │   └── EventCoordinationService.java
│   └── domain/
│       └── wydarzenie/WydarzenieAgregat.java
├── informacjeoparafii/
│   ├── application/
│   │   ├── ParishInfoFactory.java
│   │   └── ParishInformationService.java
│   └── domain/...
├── duszpasterstwowiernych/
│   ├── application/
│   │   ├── PastoralCareFactory.java
│   │   └── PastoralCareService.java
│   └── domain/
│       └── parafianin/ParafianinAgregat.java
├── grupyparafialne/
│   ├── application/
│   │   ├── ParishGroupFactory.java
│   │   └── ParishGroupService.java
│   └── domain/
│       └── grupa/GrupaParafialnaAgregat.java
├── organizacjarolizadan/
│   ├── application/
│   │   ├── StaffFactory.java
│   │   └── ParishOperationsService.java
│   └── domain/...
└── poslugasakramentalna/
    ├── application/
    │   ├── SacramentalMinistryFactory.java
    │   └── SacramentalMinistryService.java
    └── domain/...
```

---

## 9. Konfiguracja

### `application.properties`

| Właściwość | Wartość |
|------------|---------|
| `spring.datasource.url` | `jdbc:h2:file:./data/eparish` |
| `spring.jpa.hibernate.ddl-auto` | `update` |
| `spring.sql.init.mode` | `always` |
| `spring.sql.init.continue-on-error` | `true` |
| `spring.h2.console.enabled` | `true` |
| `springdoc.swagger-ui.path` | `/swagger-ui/index.html` |

### Profil testowy (`application-test.properties`)

H2 in-memory, `ddl-auto=create-drop` — izolacja między testami.

---

## 10. Polecenia

```powershell
.\gradlew.bat bootRun        # uruchomienie
.\gradlew.bat test           # testy JUnit
.\gradlew.bat build          # kompilacja + testy
.\gradlew.bat compileJava    # tylko kompilacja
```

---

## 11. Historia zmian (skrót)

| Data | Zmiana |
|------|--------|
| maj 2026 | Pełny refaktoring do DDD: 6 serwisów, 6 fabryk, 3 agregaty z metodami domenowymi; 20 UC; PUT/DELETE; Swagger UI |
| maj 2026 | `ApiIntegrationTest` (9 scenariuszy), profil testowy |
| maj 2026 | `HomeController`, `data.sql` pełny seed, `EntityIds` helper |
| maj 2026 | Wszystkie encje JPA (24), repozytoria, kontrolery bazowe |

---

*Po przejęciu: przeczytaj `README.md`, usuń `data/`, uruchom `bootRun`, odpal `gradlew test`, otwórz Swagger UI na `http://localhost:8080/swagger-ui/index.html`.*
