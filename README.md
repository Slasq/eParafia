# eParafia

System obsługi parafii — projekt zespołowy (PRz, Inżynieria i analiza danych).  
Backend: **Java 26**, **Spring Boot 4**, **JPA**, baza **H2**, API **REST**.

Pełna dokumentacja analityczna: [`docs/docs.md`](docs/docs.md)  
Repozytorium GitHub: https://github.com/Slasq/eParafia

---

## Wymagania

| Narzędzie | Wersja |
|-----------|--------|
| JDK | **26** (toolchain w `build.gradle`) |
| Gradle | wrapper w projekcie (`gradlew` / `gradlew.bat`) |
| IDE (opcjonalnie) | IntelliJ IDEA, VS Code + Extension Pack for Java |

JDK 26 można zainstalować z [adoptium.net](https://adoptium.net/) lub z instalatora w `docs/` (jeśli jest w repozytorium).

---

## Uruchomienie

### 1. IntelliJ IDEA

1. **File → Open** → katalog projektu (folder z `build.gradle`).
2. Poczekaj na import Gradle i pobranie zależności.
3. **File → Project Structure → Project** → SDK: **Java 26**.
4. **Settings → Build Tools → Gradle** → **Gradle JVM**: Java 26.
5. Włącz przetwarzanie adnotacji Lombok:  
   **Settings → Compiler → Annotation Processors → Enable annotation processing**.
6. Otwórz `src/main/java/edu/prz/eparish/EparishApplication.java` i uruchom **Run** przy metodzie `main`.

W konfiguracji uruchomienia ustaw **Working directory** na katalog projektu (`$PROJECT_DIR$`), żeby baza plikowa H2 trafiała do `./data/`.

### 2. Terminal (Windows)

```powershell
cd ścieżka\do\eParafia
.\gradlew.bat bootRun
```

### 3. JAR

```powershell
.\gradlew.bat bootJar
java -jar build\libs\eparish-0.0.1-SNAPSHOT.jar
```

Aplikacja startuje domyślnie na **http://localhost:8080**.

---

## Baza danych (H2)

- Plik bazy: `./data/eparish` (katalog `data/` jest w `.gitignore`).
- Przy starcie Hibernate tworzy/aktualizuje schemat (`ddl-auto=update`), a `data.sql` ładuje dane startowe.
- Konsola H2: **http://localhost:8080/h2-console**

| Pole | Wartość |
|------|---------|
| JDBC URL | `jdbc:h2:file:./data/eparish` |
| User | `sa` |
| Password | *(puste)* |

---

## API REST

- Strona startowa (JSON): **GET /** — lista przykładowych ścieżek
- Wszystkie zasoby: prefiks **`/api`**
- Tworzenie zasobów: **201 Created**, odczyt: **200 OK**

### Odczyt (GET) — działa w przeglądarce

| Ścieżka | Opis |
|---------|------|
| `/api/parishes` | Parafie |
| `/api/parishioners` | Parafianie |
| `/api/families` | Rodziny |
| `/api/groups` | Grupy parafialne |
| `/api/events` | Wydarzenia |
| `/api/intentions` | Intencje |
| `/api/announcements` | Ogłoszenia |
| `/api/employees` | Pracownicy |
| `/api/priests` | Księża |
| `/api/sacraments` | Sakramenty |
| `/api/positions` | Stanowiska |
| `/api/event-types` | Typy wydarzeń |
| `/api/schedules` | Harmonogramy |

Szczegóły pojedynczego rekordu: np. `/api/parishes/1`, `/api/events/1`.

### Tworzenie (POST) — Bruno / Postman

| Ścieżka | Opis |
|---------|------|
| `/api/families` | Rodzina |
| `/api/family-addresses` | Adres rodziny |
| `/api/groups` | Grupa |
| `/api/memberships` | Członkostwo w grupie |
| `/api/parishioners` | Parafianin |
| `/api/records` | Kartoteka |
| `/api/documents` | Dokument |
| `/api/dioceses` | Diecezja |
| `/api/localities` | Miejscowość |
| `/api/employees` | Pracownik |
| `/api/duties` | Obowiązek |
| `/api/intentions` | Intencja |
| `/api/announcements` | Ogłoszenie |
| `/api/events` | Wydarzenie |
| `/api/schedules` | Harmonogram |
| `/api/offerings` | Ofiara |
| `/api/participants` | Uczestnik wydarzenia |
| `/api/organizers` | Organizator |
| `/api/priests` | Ksiądz |
| `/api/sacraments` | Sakrament |
| `/api/sacrament-administrations` | Udzielenie sakramentu |

### Przykłady JSON

**Rodzina** — `POST /api/families`

```json
{ "familyName": "Kowalscy", "memberCount": 4 }
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

**Udzielenie sakramentu** — `POST /api/sacrament-administrations` (dane startowe: parafianin 1, ksiądz 1, sakrament 1)

```json
{
  "administrationDate": "2026-05-25",
  "parishionerId": 1,
  "priestId": 1,
  "sacramentId": 1
}
```

**Intencja** — `POST /api/intentions` (`eventId`: 1 z `data.sql`)

```json
{
  "content": "Za zdrowie rodziny Kowalskich",
  "date": "2026-05-30",
  "donor": "Jan Kowalski",
  "eventId": 1
}
```

Kontrolery w pakiecie `edu.prz.eparish.api`: `HomeController`, `PastoralCareController`, `ParishOperationsController`, `ParishInformationController`, `EventCoordinationController`, `SacramentalMinistryController`.

---

## Testy API (Bruno)

Kolekcja w katalogu [`tests/`](tests/). Narzędzie: [Bruno](https://www.usebruno.com/).

1. Uruchom aplikację (`bootRun` lub IntelliJ).
2. W Bruno: **Open Collection** → wybierz folder `tests/`.
3. Uruchom żądania w podfolderach (np. `Pastoral Care/Add Family`).

Każdy test zakłada działający serwer na `http://localhost:8080`.

Wytyczne testera: [`docs/wytyczne_do_testów.md`](docs/wytyczne_do_testów.md).

---

## Struktura kodu

```
src/main/java/edu/prz/eparish/
├── EparishApplication.java          # punkt wejścia Spring Boot
├── api/                             # kontrolery REST
├── duszpasterstwowiernych/          # rodziny, parafianie, adresy
├── grupyparafialne/                 # grupy, członkostwa
├── informacjeoparafii/              # parafia, diecezja, kartoteki, dokumenty
├── koordynacjawydarzen/             # wydarzenia, intencje, ogłoszenia, …
├── organizacjarolizadan/            # pracownicy, stanowiska, obowiązki
└── poslugasakramentalna/            # księża, sakramenty
```

Model dziedzinowy: ok. **24 encje JPA** w podziałach DDD (wymaganie projektu: 20 encji — spełnione).

---

## Polecenia Gradle

```powershell
.\gradlew.bat build          # kompilacja + testy
.\gradlew.bat test           # tylko testy JUnit
.\gradlew.bat bootRun        # uruchomienie aplikacji
```

---

## Zespół — co dalej

- Rozbudowa testów Bruno pod nowe endpointy.
- Osobne **skrypty DDL** (wymaganie projektu obok JPA).
- Uzupełnienie sekcji **Projekt**, **Implementacja**, **Testy** w `docs/docs.md` oraz raport z testów.
- Ewentualnie PUT/DELETE i walidacja żądań.

---

## Prezentacja

Termin (wg dokumentacji): **16–17.06.2026**.
