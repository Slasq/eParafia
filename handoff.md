# eParafia — przekazanie projektu (handoff)

Dokument dla zespołu przejmującego dalszą pracę. Ostatnia aktualizacja stanu: **czerwiec 2026**.

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

Stack: **Java 26**, **Spring Boot 4.0.6**, **JPA/Hibernate 7**, **H2** (plik), **Lombok**, **Gradle 9.4**, **springdoc-openapi 2.8.9**, **Jackson 3** (`tools.jackson`).

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
- **ListFilterSupport** (`api/support/ListFilterSupport.java`) — helper filtrowania list po opcjonalnych parametrach query (używany w serwisach przy `list*`).
- **PatchBodySupport** (`api/support/PatchBodySupport.java`) — parsowanie body PATCH parafii/parafianina (Jackson 3, jawne `null` na relacjach).

### API — PATCH, DELETE, filtrowanie

Od czerwca 2026 każdy główny zasób listowy obsługuje pełny CRUD-lite:

| Operacja | Opis |
|----------|------|
| **GET** (lista) | Opcjonalne query params — filtrowanie po polach encji i kluczach obcych (AND) |
| **GET** `/{id}` | Pojedynczy rekord (gdzie dotyczy) |
| **POST** | Tworzenie |
| **PUT** | Pełna aktualizacja (tam, gdzie była wcześniej) |
| **PATCH** | Częściowa aktualizacja — tylko pola obecne w body; jawne `null` na `familyId` / `localityId` czyści przypisanie |
| **DELETE** | Usunięcie — odpowiedź `204 No Content` |

**GET `/{id}`** — dostępne m.in. dla: `events`, `schedules`, `dioceses`, `documents`, `positions`, `sacraments`, `parishes`, `parishioners`, `offerings`, `sacrament-administrations`, `employees`, `priests`, `groups`, `families`, `intentions`, `announcements`.

**POST bezpośredni (ID w body)** — oprócz ścieżek zagnieżdżonych (`/api/events/{id}/…`) działają też: `POST /api/intentions`, `/api/announcements`, `/api/offerings`, `/api/participants`, `/api/organizers` (pole `eventId` w JSON).

Przykład testowy (ogłoszenia wydarzenia):

```
GET  /api/announcements?eventId=1     → tylko ogłoszenia wydarzenia 1
PATCH /api/announcements/3  {"content":"..."}
DELETE /api/announcements/3
```

Pełna tabela parametrów filtrowania: [`README.md` — sekcja „Filtrowanie list”](README.md#filtrowanie-list-query-params).

Endpointy specjalne bez zmian: `PUT .../realize`, `PUT .../complete`, `PUT .../terminate`, `PUT /families/{id}/name`.

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
| PATCH / DELETE na wszystkich głównych zasobach listowych | ✅ |
| Filtrowanie list GET po query params (`ListFilterSupport`) | ✅ |
| PUT / DELETE na kluczowych zasobach (wcześniejsze) | ✅ |
| Swagger UI (`/swagger-ui/index.html`) | ✅ działający na Java 26 |
| `OpenApiConfig` z pełną dokumentacją UC | ✅ |
| `data.sql` — pełny seed | ✅ |
| `EntityIds` — helper generowania ID | ✅ |
| Pole `status` na `Obowiazek` (ASSIGNED/COMPLETED) | ✅ |
| Pole `status` na `Intencja` (PLANNED/REALIZED) | ✅ |
| Poprawki endpointów po testach (cze 2026, patrz §12) | ✅ |

### Serwisy i przypadki użycia

| Serwis | UC (metody) |
|--------|------------|
| `EventCoordinationService` | createEvent, updateEvent, **patchEvent**, deleteEvent, assignIntention, **patchIntention**, **deleteIntention**, **realizeIntention**, addAnnouncement, **patchAnnouncement**, **deleteAnnouncement**, recordOffering, **patchOffering**, **deleteOffering**, **getOffering**, assignParticipant, **patchParticipant**, **removeParticipant**, assignOrganizer, **patchOrganizer**, **removeOrganizer**, **patchEventType**, **deleteEventType**, **patchSchedule**, **deleteSchedule**, **getSchedule**, getEventAggregate, **list\*** z filtrami |
| `ParishInformationService` | addDiocese, updateDiocese, **patchDiocese**, **deleteDiocese**, **getDiocese**, addLocality, **updateLocality**, **patchLocality**, **deleteLocality**, addParish, updateParish, **patchParish**, **deleteParish**, registerParishioner, updateParishioner, **patchParishioner** (w tym `familyId: null`), deleteParishioner, createRecord, updateRecord, **patchRecord**, deleteRecord, addDocument, **patchDocument**, **deleteDocument**, **getDocument**, getParishionerAggregate, **list\*** z filtrami |
| `PastoralCareService` | addFamily, updateFamilyName, **patchFamily**, **deleteFamily**, assignParishionerToFamily, addFamilyAddress (walidacja `familyId`, konflikt 409; też `POST /families/{id}/addresses`), updateFamilyAddress, **patchFamilyAddress**, **deleteFamilyAddress**, **list\*** z filtrami |
| `ParishGroupService` | createGroup, updateGroup, **patchGroup**, **deleteGroup**, addMembership, **patchMembership**, **deleteMembership**, terminateMembership, getGroupAggregate, **list\*** z filtrami |
| `ParishOperationsService` | addEmployee, **patchEmployee**, **deleteEmployee**, addPosition, **patchPosition**, **deletePosition**, **getPosition**, assignDuty, **patchDuty**, **deleteDuty**, **completeDuty**, **list\*** z filtrami |
| `SacramentalMinistryService` | addPriest, **patchPriest**, **deletePriest**, addSacrament, **patchSacrament**, **deleteSacrament**, **getSacrament**, registerSacrament, **patchAdministration**, **deleteAdministration**, **list\*** z filtrami |

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
| 1 | diecezja, miejscowość, parafia, stanowiska (1–2), pracownik, obowiązki (1–2), typy wydarzeń, harmonogramy (1–2), **wydarzenia (1–2)**, intencje (1–3), ogłoszenia (1–3), ofiary (1–3), uczestnicy (1–3), organizatorzy (1–2), grupy (1–2), członkostwa (1–2), sakramenty (1–7), księża (1–2), rodziny (1–2), adres rodziny, parafianie (1–3), kartoteka + dokumenty, udzielanie sakramentu |

Kluczowe zależności w testach:
- **Add Intention / Add Announcement** → wymagają `eventId: 1`
- **List Announcements (filtrowanie)** → `GET /api/announcements?eventId=1` zwraca id 1–2 (nie id 3 — to event 2)
- **List Intentions (filtrowanie)** → `GET /api/intentions?eventId=1&status=PLANNED` → intencja id=1
- **Register Sacrament** → `parishionerId: 1`, `priestId: 1`, `sacramentId: 1`
- **Add Employee** → `parishId: 1`, `positionId: 1`
- **Documents filter** → `GET /api/documents?recordId=1` → dokumenty parafianina Jan Kowalski
- **Add Family Address** → `POST /api/family-addresses` z `familyId: 2` lub `POST /api/families/2/addresses` (rodzina 1 ma już adres w seedzie)
- **POST Parish** → pole `localityId` (alias `localitiesId`); wiele parafii może wskazywać tę samą miejscowość

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

Brakuje requestów Bruno dla wielu endpointów — do dopisania przez testera, m.in.:
- filtrowanie list (`GET ...?eventId=1`, `GET ...?parishId=1` itd.)
- PATCH / DELETE na zasobach (np. announcements, intentions, offerings)
- offerings, participants, organizers, duties, aggregate GET-y

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
- Filtrowanie działa w pamięci / przez wąskie zapytania repo — przy dużej bazie warto rozważyć paginację lub Criteria API.

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
│       ├── ListFilterSupport.java
│       ├── PatchBodySupport.java       ← PATCH parafii/parafianina (Jackson 3)
│       ├── ApiExceptionHandler.java
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
| cze 2026 | Poprawki rundy 2 (PATCH 500, adres rodziny) — patrz §12 |
| cze 2026 | Poprawki rundy 1 (GET/POST brakujące endpointy) — patrz §12 |
| cze 2026 | PATCH + DELETE na wszystkich głównych zasobach; filtrowanie list GET po query params; `ListFilterSupport`; GET by id dla `offerings` i `sacrament-administrations` |
| maj 2026 | Pełny refaktoring do DDD: 6 serwisów, 6 fabryk, 3 agregaty z metodami domenowymi; 20 UC; PUT/DELETE; Swagger UI |
| maj 2026 | `ApiIntegrationTest` (9 scenariuszy), profil testowy |
| maj 2026 | `HomeController`, `data.sql` pełny seed, `EntityIds` helper |
| maj 2026 | Wszystkie encje JPA (24), repozytoria, kontrolery bazowe |

---

## 12. Poprawki po testach (cze 2026)

Zgłoszenia testera i wprowadzone zmiany:

| Problem | Rozwiązanie |
|---------|-------------|
| `GET /api/schedules/{id}` → 405 | Dodano endpoint GET + `EventCoordinationService.getSchedule()` |
| `POST /api/participants`, `/api/organizers`, `/api/offerings` → 405 | Dodano bezpośredni POST z `eventId` w body (jak intencje/ogłoszenia) |
| `GET /api/positions/{id}`, `/api/documents/{id}`, `/api/sacraments/{id}`, `/api/dioceses/{id}` → 405 | Dodano GET po ID w kontrolerach i serwisach |
| `POST /api/parishes` z `localityId` → 500 | Zmiana relacji `Parafia`→`Miejscowosc` z `@OneToOne` na `@ManyToOne` |
| `localityId` null w odpowiedzi przy `localitiesId` w body | `@JsonAlias("localitiesId")` na requestach parafii |
| `PATCH /api/parishioners/{id}` z `familyId: null` nie czyścił rodziny | PATCH rozpoznaje jawne `null` i ustawia `rodzina = null` |
| `PATCH /api/parishes/{id}` — to samo dla `localityId` | Analogiczna obsługa jawnego `null` na `localityId` |
| `POST /api/family-addresses` → 404 | Walidacja: brak `familyId` → 400; rodzina z adresem → 409; aliasy `rodzinaId` / `rodzina_id`; dodano `POST /api/families/{familyId}/addresses` |

**Runda 2 (cze 2026):**

| Problem | Rozwiązanie |
|---------|-------------|
| `PATCH /api/parishioners/{id}` → 500 | Usunięto `JsonNode` (Jackson 2); `PatchBodySupport` + `Map` body; obsługa `familyId` i `familyId: null` |
| `PATCH /api/parishes/{id}` → 500 | Jak wyżej; obsługa `localityId`, `localitiesId` i `localityId: null` |
| `ObjectMapper` bean not found (build) | Brak wstrzykiwania `ObjectMapper` — helper statyczny w `PatchBodySupport` |

Zmienione pliki (główne): kontrolery `api/`, serwisy, `Parafia.java`, `AdresRodzinyRepozytorium.java`, `api/support/PatchBodySupport.java`, `api/support/ApiExceptionHandler.java`.

---

*Po przejęciu: przeczytaj `README.md`, usuń `data/`, uruchom `bootRun`, odpal `gradlew test`, otwórz Swagger UI na `http://localhost:8080/swagger-ui/index.html`.*
