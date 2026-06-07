# eParafia

System obsługi parafii — projekt zespołowy (PRz, Inżynieria i analiza danych L2).  
Backend: **Java 26**, **Spring Boot 4.0.6**, **JPA / Hibernate**, baza **H2**, API **REST**.

Pełna dokumentacja analityczna: [`docs/docs.md`](docs/docs.md)  
Przekazanie projektu: [`handoff.md`](handoff.md)  
Repozytorium GitHub: https://github.com/Slasq/eParafia

---

## Wymagania

| Narzędzie | Wersja |
|-----------|--------|
| JDK | **26** (toolchain w `build.gradle`) |
| Gradle | wrapper w projekcie (`gradlew.bat`) |
| IDE (opcjonalnie) | IntelliJ IDEA, VS Code + Extension Pack for Java |

---

## Uruchomienie

### Terminal (Windows)

```powershell
cd ścieżka\do\eParafia
.\gradlew.bat bootRun
```

### IntelliJ IDEA

1. **File → Open** → katalog projektu.
2. **File → Project Structure → Project** → SDK: Java 26.
3. **Settings → Build Tools → Gradle → Gradle JVM**: Java 26.
4. **Settings → Compiler → Annotation Processors → Enable annotation processing** (Lombok).
5. Uruchom `EparishApplication.java` → **Working directory**: `$PROJECT_DIR$`.

### JAR

```powershell
.\gradlew.bat bootJar
java -jar build\libs\eparish-0.0.1-SNAPSHOT.jar
```

Aplikacja startuje na **http://localhost:8080**.

---

## Baza danych (H2)

| | |
|---|---|
| Plik bazy | `./data/eparish` (`data/` jest w `.gitignore`) |
| Konsola H2 | http://localhost:8080/h2-console |
| JDBC URL | `jdbc:h2:file:./data/eparish` |
| User / hasło | `sa` / *(puste)* |

Przy starcie Hibernate tworzy schemat (`ddl-auto=update`), a `data.sql` ładuje dane startowe (diecezja, miejscowość, parafia, personel, **2 wydarzenia**, intencje, ogłoszenia, ofiary, uczestnicy, organizatorzy, grupy, członkostwa, sakramenty 1–7, księża, rodziny, parafianie 1–3, kartoteka, dokumenty, udzielanie sakramentu).

**Jeśli testy Bruno dają 404 lub brak danych seed** — usuń folder `data/` i uruchom aplikację od nowa (świeża baza + pełny seed).

---

## Swagger UI

Interaktywna dokumentacja API (wszystkie endpointy, modele, możliwość testowania):

**http://localhost:8080/swagger-ui/index.html**

Działa na Java 26 — sprawdzone.

---

## Architektura

Projekt stosuje **Domain-Driven Design (DDD)** z czystym podziałem warstw:

```
HTTP Request
    ↓
Controller        ← tylko mapowanie request/response, zero logiki
    ↓
Service           ← przypadki użycia (use cases), walidacja, orkiestracja
    ↓
Factory           ← tworzenie obiektów domenowych (new + EntityIds)
    ↓
Repository (JPA)  ← persystencja
```

### Konteksty domenowe

| Pakiet | Serwis | Fabryka | Agregat |
|---|---|---|---|
| `koordynacjawydarzen` | `EventCoordinationService` | `EventFactory` | `WydarzenieAgregat` *(złożony)* |
| `informacjeoparafii` | `ParishInformationService` | `ParishInfoFactory` | `ParafianinAgregat` |
| `duszpasterstwowiernych` | `PastoralCareService` | `PastoralCareFactory` | — |
| `grupyparafialne` | `ParishGroupService` | `ParishGroupFactory` | `GrupaParafialnaAgregat` |
| `organizacjarolizadan` | `ParishOperationsService` | `StaffFactory` | — |
| `poslugasakramentalna` | `SacramentalMinistryService` | `SacramentalMinistryFactory` | — |

### Agregaty domenowe

| Agregat | Korzeń | Encje składowe | Endpoint |
|---|---|---|---|
| **WydarzenieAgregat** *(złożony)* | WydarzenieParafialne | Intencja, Ogloszenie, Ofiara, Uczestnik, Organizator | `GET /api/events/{id}/aggregate` |
| **ParafianinAgregat** | Parafianin | Kartoteka, Dokument | `GET /api/parishioners/{id}/aggregate` |
| **GrupaParafialnaAgregat** | GrupaParafialna | Czlonkostwo | `GET /api/groups/{id}/aggregate` |

---

## API REST

Strona startowa z listą wszystkich use-cases: **GET /**  
Prefix wszystkich zasobów: **`/api`**

Każdy zasób listowy obsługuje **GET** (z opcjonalnym filtrowaniem), **POST**, **PATCH** (częściowa aktualizacja) oraz **DELETE** (usuwanie). Pełna aktualizacja nadal dostępna przez **PUT** tam, gdzie była wcześniej.

### Filtrowanie list (query params)

Wszystkie parametry są **opcjonalne** i można je **łączyć** (logika AND). Bez parametrów — zwracana jest pełna lista.

Przykład — ogłoszenia tylko dla wydarzenia id=1:

```
GET /api/announcements?eventId=1
```

| Endpoint | Parametry filtrowania |
|----------|----------------------|
| `/events` | `parishId`, `eventTypeId`, `scheduleId`, `name` |
| `/intentions` | `eventId`, `status`, `date`, `donor` |
| `/announcements` | `eventId` |
| `/offerings` | `eventId`, `type`, `date` |
| `/participants` | `eventId`, `role` |
| `/organizers` | `eventId`, `role` |
| `/event-types` | `name` |
| `/schedules` | `date` |
| `/dioceses` | `name`, `see`, `bishop` |
| `/localities` | `dioceseId`, `province`, `postalCode`, `name` |
| `/parishes` | `localityId`, `name` |
| `/parishioners` | `parishId`, `familyId`, `pesel`, `firstName`, `lastName` |
| `/records` | `parishionerId` |
| `/documents` | `recordId`, `type` |
| `/families` | `familyName` |
| `/family-addresses` | `familyId`, `city`, `postalCode` |
| `/groups` | `name`, `supervisor` |
| `/memberships` | `groupId`, `parishionerId` |
| `/employees` | `parishId`, `positionId` |
| `/positions` | `name` |
| `/duties` | `positionId`, `status` |
| `/priests` | `parishId`, `role` |
| `/sacraments` | `name` |
| `/sacrament-administrations` | `parishionerId`, `priestId`, `sacramentId`, `administrationDate` |

### PATCH i DELETE

**PATCH** — częściowa aktualizacja: w body wysyłasz tylko pola do zmiany; pola pominięte pozostają bez zmian.

Wyjątek — jawne `null` czyści przypisanie:
- `PATCH /api/parishioners/{id}` z `{"familyId": null}` → usuwa parafianina z rodziny
- `PATCH /api/parishes/{id}` z `{"localityId": null}` → usuwa przypisanie miejscowości

```http
PATCH /api/announcements/3
{"content": "Nowa treść ogłoszenia"}
```

```http
PATCH /api/parishioners/1
{"familyId": null}
```

**DELETE** — usuwa rekord, odpowiedź **204 No Content**.

```http
DELETE /api/announcements/3
```

PATCH/DELETE dostępne m.in. dla: `events`, `intentions`, `announcements`, `offerings`, `participants`, `organizers`, `event-types`, `schedules`, `dioceses`, `localities`, `parishes`, `parishioners`, `records`, `documents`, `families`, `family-addresses`, `groups`, `memberships`, `employees`, `positions`, `duties`, `priests`, `sacraments`, `sacrament-administrations`.

Endpointy specjalne (bez zmian): `PUT /api/events/{eId}/intentions/{iId}/realize`, `PUT /api/duties/{id}/complete`, `PUT /api/memberships/{id}/terminate`, `PUT /api/families/{id}/name`.

### Przypadki użycia → endpointy

| Use Case | Metoda | Ścieżka |
|---|---|---|
| Zarządzanie wydarzeniami | POST / PUT / PATCH / DELETE | `/api/events` |
| Przypisanie intencji | POST / PATCH / DELETE | `/api/intentions`, `/api/events/{id}/intentions` |
| Prowadzenie harmonogramu (realizacja intencji) | PUT | `/api/events/{eId}/intentions/{iId}/realize` |
| Zarządzanie ogłoszeniami | POST / PATCH / DELETE | `/api/announcements`, `/api/events/{id}/announcements` |
| Ewidencja ofiar | POST / PATCH / DELETE | `/api/offerings`, `/api/events/{id}/offerings` |
| Przypisanie uczestników | POST / PATCH / DELETE | `/api/participants`, `/api/events/{id}/participants` |
| Przypisanie organizatorów | POST / PATCH / DELETE | `/api/organizers`, `/api/events/{id}/organizers` |
| Zarządzanie parafią | POST / PUT / PATCH / DELETE | `/api/parishes` |
| Dodaj diecezję / biskupa / siedzibę | POST / PUT / PATCH / DELETE | `/api/dioceses` |
| Dodaj miejscowość | POST / PUT / PATCH / DELETE | `/api/localities` |
| Zarządzanie parafianami | POST / PUT / PATCH / DELETE | `/api/parishioners` |
| Prowadzenie kartotek | POST / PATCH / DELETE | `/api/records`, `/api/parishioners/{id}/record` |
| Rejestracja zdarzeń religijnych | PUT / PATCH | `/api/records/{id}` |
| Zarządzanie dokumentacją | POST / PATCH / DELETE | `/api/documents`, `/api/parishioners/{id}/record/documents` |
| Zarządzanie wspólnotą — dodaj rodzinę | POST / PATCH / DELETE | `/api/families` |
| Zmiana nazwiska rodziny | PUT | `/api/families/{id}/name` |
| Przypisanie do rodziny | PUT | `/api/parishioners/{pid}/family/{fid}` |
| Dodanie adresu rodziny | POST / PATCH / DELETE | `/api/family-addresses`, `/api/families/{familyId}/addresses` |
| Zmiana adresu rodziny | PUT | `/api/family-addresses/{id}` |
| Dodaj grupę | POST / PATCH / DELETE | `/api/groups` |
| Zmień grupę | PUT | `/api/groups/{id}` |
| Dodaj członkostwo (z datami) | POST / PATCH / DELETE | `/api/memberships` |
| Zakończ członkostwo | PUT | `/api/memberships/{id}/terminate` |
| Zarządzanie personelem | POST / PATCH / DELETE | `/api/employees` |
| Przydzielanie stanowiska | POST / PATCH / DELETE | `/api/positions` |
| Przydzielanie obowiązku | POST / PATCH / DELETE | `/api/duties` |
| Wykonanie obowiązku | PUT | `/api/duties/{id}/complete` |
| Rejestrowanie sakramentów | POST / PATCH / DELETE | `/api/sacrament-administrations` |

### Odczyt (GET)

Listy z opcjonalnym filtrowaniem (patrz sekcja **Filtrowanie list** powyżej):

```
/api/parishes            /api/parishioners         /api/families
/api/groups              /api/memberships           /api/family-addresses
/api/events              /api/intentions            /api/announcements
/api/offerings           /api/participants          /api/organizers
/api/schedules           /api/event-types           /api/priests
/api/sacraments          /api/sacrament-administrations
/api/employees           /api/positions             /api/duties
/api/dioceses            /api/localities            /api/records
/api/documents
```

Szczegół po ID (`GET /api/{zasób}/{id}`):

| Zasób | Przykład |
|-------|----------|
| Wydarzenia, parafie, parafianie, intencje, ogłoszenia, ofiary | `/api/events/1`, `/api/parishes/1`, `/api/parishioners/1`, `/api/intentions/1`, `/api/announcements/1`, `/api/offerings/1` |
| Harmonogramy, diecezje, dokumenty, stanowiska, sakramenty | `/api/schedules/1`, `/api/dioceses/1`, `/api/documents/1`, `/api/positions/1`, `/api/sacraments/1` |
| Udzielanie sakramentu, pracownicy, księża, grupy, rodziny | `/api/sacrament-administrations/1`, `/api/employees/1`, `/api/priests/1`, `/api/groups/1`, `/api/families/1` |

### POST bezpośredni (eventId / recordId w body)

Niektóre zasoby można tworzyć dwoma ścieżkami — zagnieżdżoną lub z ID w body (jak intencje i ogłoszenia):

| Zasób | Zagnieżdżony POST | Bezpośredni POST (ID w body) |
|-------|-------------------|------------------------------|
| Intencja | `POST /api/events/{id}/intentions` | `POST /api/intentions` — pole `eventId` |
| Ogłoszenie | `POST /api/events/{id}/announcements` | `POST /api/announcements` — pole `eventId` |
| Ofiara | `POST /api/events/{id}/offerings` | `POST /api/offerings` — pole `eventId` |
| Uczestnik | `POST /api/events/{id}/participants` | `POST /api/participants` — pole `eventId` |
| Organizator | `POST /api/events/{id}/organizers` | `POST /api/organizers` — pole `eventId` |

### Uwagi dla testerów

**Parafia — pole miejscowości:** w body używaj `localityId` (alias JSON: `localitiesId`). Relacja parafia–miejscowość to `@ManyToOne` — wiele parafii może być w jednej miejscowości.

**Adres rodziny:** wymagane pole `familyId` (aliasy: `rodzinaId`, `rodzina_id`). Dwa endpointy POST:
- `POST /api/family-addresses` — `familyId` w body
- `POST /api/families/{familyId}/addresses` — `familyId` w ścieżce

Model 1:1 — jedna rodzina ma jeden adres. W seedzie rodzina id=1 ma już adres; do testu POST użyj rodziny **bez adresu** (np. `familyId: 2`). Brak `familyId` → `400`, duplikat adresu → `409`.

**PATCH parafii / parafianina:** działa częściowa aktualizacja pól oraz jawne `null` na `localityId` / `familyId` (odpinanie relacji). Implementacja przez `PatchBodySupport` (kompatybilność ze Spring Boot 4 / Jackson 3).

### Przykłady JSON

**Wydarzenie** — `POST /api/events`
```json
{
  "name": "Msza niedzielna",
  "dateTime": "2026-06-01T10:00:00",
  "place": "Kościół główny",
  "description": "Msza z okazji Zielonych Świątek",
  "parishId": 1,
  "eventTypeId": 1,
  "scheduleId": 1
}
```

**Intencja** — `POST /api/intentions`
```json
{
  "content": "Za zdrowie rodziny Kowalskich",
  "date": "2026-05-30",
  "donor": "Jan Kowalski",
  "eventId": 1
}
```

**Parafianin** — `POST /api/parishioners`
```json
{
  "firstName": "Anna",
  "lastName": "Nowak",
  "pesel": "95050512345",
  "birthDate": "1995-05-05",
  "phone": "500600700",
  "email": "anna@example.com",
  "parishId": 1,
  "familyId": 1
}
```

**Rejestracja sakramentu** — `POST /api/sacrament-administrations`
```json
{
  "administrationDate": "2026-05-25",
  "parishionerId": 1,
  "priestId": 1,
  "sacramentId": 1
}
```

**Wykonanie obowiązku** — `PUT /api/duties/1/complete`  
*(brak body — status zmienia się na `COMPLETED`)*

**Filtrowanie ogłoszeń wydarzenia** — `GET /api/announcements?eventId=1`

**Częściowa aktualizacja ogłoszenia** — `PATCH /api/announcements/1`
```json
{"content": "Zaktualizowana treść ogłoszenia"}
```

**Usunięcie intencji** — `DELETE /api/intentions/1` → odpowiedź `204 No Content`

**Uczestnik (bezpośredni POST)** — `POST /api/participants`
```json
{
  "firstName": "Maria",
  "lastName": "Kowalska",
  "role": "Lektorka",
  "eventId": 1
}
```

**Ofiara (bezpośredni POST)** — `POST /api/offerings`
```json
{
  "amount": 50.00,
  "date": "2026-06-01",
  "type": "COLLECTION",
  "eventId": 1
}
```

**Przypisanie parafianina do rodziny** — `PATCH /api/parishioners/2`
```json
{"familyId": 1}
```

**Odpięcie parafianina od rodziny** — `PATCH /api/parishioners/2`
```json
{"familyId": null}
```

**Zmiana miejscowości parafii** — `PATCH /api/parishes/1`
```json
{"localityId": 1}
```

**Adres rodziny** — `POST /api/family-addresses`
```json
{
  "street": "ul. Testowa",
  "houseNumber": "7",
  "postalCode": "35-002",
  "city": "Rzeszow",
  "familyId": 2
}
```

---

## Testy

### Automatyczne (JUnit + MockMvc)

```powershell
.\gradlew.bat test
```

Plik: `src/test/java/edu/prz/eparish/api/ApiIntegrationTest.java` — 9 scenariuszy pokrywających główne przepływy.

### Bruno (HTTP)

Kolekcja w katalogu [`tests/`](tests/). Narzędzie: [usebruno.com](https://www.usebruno.com/).

1. Uruchom aplikację.
2. W Bruno: **Open Collection** → folder `tests/`.
3. Pierwsze żądanie zawsze: `Event_Coordination/List Events` — sprawdza seed.

Wytyczne testera: [`docs/wytyczne_do_testów.md`](docs/wytyczne_do_testów.md)

---

## Struktura kodu

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
│       ├── ListFilterSupport.java      ← filtrowanie list po query params
│       ├── PatchBodySupport.java       ← parsowanie body PATCH (Jackson 3)
│       ├── ApiExceptionHandler.java    ← mapowanie błędów walidacji na 400
│       └── OpenApiConfig.java
├── koordynacjawydarzen/
│   ├── application/
│   │   ├── EventFactory.java
│   │   └── EventCoordinationService.java
│   └── domain/
│       └── wydarzenie/WydarzenieAgregat.java  ← złożony agregat
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

**24 encje JPA** (wymaganie projektu: 20 — spełnione).

---

## Polecenia Gradle

```powershell
.\gradlew.bat bootRun        # uruchomienie
.\gradlew.bat test           # testy JUnit
.\gradlew.bat build          # kompilacja + testy
.\gradlew.bat compileJava    # tylko kompilacja
```

---

## Historia zmian (skrót)

| Data | Zmiana |
|------|--------|
| cze 2026 | Poprawki rundy 2: PATCH parafii/parafianina naprawiony (500 → 200, `PatchBodySupport` + Jackson 3); `POST /api/families/{familyId}/addresses` |
| cze 2026 | Poprawki rundy 1: GET `/{id}`, bezpośredni POST `offerings`/`participants`/`organizers`; PATCH `familyId`/`localityId: null`; parafia–miejscowość `@ManyToOne`; walidacja adresu rodziny |
| cze 2026 | PATCH + DELETE na głównych zasobach; filtrowanie list GET; `ListFilterSupport` |
| maj 2026 | Refaktoring DDD: 6 serwisów, 6 fabryk, 3 agregaty, 20 UC, Swagger UI |

---

## Prezentacja

Termin: **16–17.06.2026**
