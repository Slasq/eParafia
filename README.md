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

**PATCH** — częściowa aktualizacja: w body wysyłasz tylko pola do zmiany; pola `null`/pominięte pozostają bez zmian.

```http
PATCH /api/announcements/3
{"content": "Nowa treść ogłoszenia"}
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
| Dodanie adresu rodziny | POST / PATCH / DELETE | `/api/family-addresses` |
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

Szczegół po ID: np. `/api/events/1`, `/api/parishioners/1`, `/api/offerings/1`, `/api/sacrament-administrations/1`.

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

## Prezentacja

Termin: **16–17.06.2026**
