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
**Front nie jest wymagany** w `docs/docs.md` — wystarczą API + Bruno + ewentualnie H2 Console na demo.

Stack: **Java 26**, **Spring Boot 4.0.6**, **JPA/Hibernate**, **H2** (plik), **Lombok**, **Gradle 9.4**.

---

## 2. Szybki start (dla nowej osoby)

```powershell
cd ścieżka\do\eParafia
# JDK 26 — Project Structure / Gradle JVM w IntelliJ
.\gradlew.bat bootRun
```

- Aplikacja: http://localhost:8080  
- Indeks API: http://localhost:8080/  
- H2: http://localhost:8080/h2-console → JDBC `jdbc:h2:file:./data/eparish`, user `sa`, hasło puste  

**Testy automatyczne:**

```powershell
.\gradlew.bat test
```

**Bruno:** Open Collection → folder [`tests/`](tests/).

### Ważne: świeża baza

Jeśli testy intencji/ogłoszeń zwracają **404** („Wydarzenie nie istnieje”):

1. Zatrzymaj aplikację.  
2. Usuń folder **`data/`** w katalogu projektu.  
3. Uruchom ponownie — `data.sql` załaduje seed (m.in. **wydarzenie id=1**).  

W Bruno najpierw odpal **`Event_Coordination/List Events`** — sprawdza, czy seed jest OK.

---

## 3. Co jest już zrobione

### Backend

| Obszar | Status |
|--------|--------|
| ~24 encje JPA w pakietach DDD | ✅ |
| Repozytoria Spring Data | ✅ |
| 6 kontrolerów REST w `edu.prz.eparish.api` | ✅ |
| `data.sql` — dane startowe | ✅ |
| `GET /` — JSON z podpowiedzią API (koniec Whitelabel 404) | ✅ |
| Helper `api/support/EntityIds.java` (generowanie ID) | ✅ |
| `@Column` na `WydarzenieParafialne.dataIGodzina` (zgodność z `data.sql`) | ✅ |

**Kontrolery:**

| Plik | Odpowiedzialność |
|------|------------------|
| `HomeController` | `GET /` |
| `PastoralCareController` | rodziny, grupy, członkostwa, adresy rodzin |
| `ParishOperationsController` | pracownicy, stanowiska, obowiązki, intencje, ogłoszenia |
| `ParishInformationController` | diecezje, miejscowości, parafie, parafianie, kartoteki, dokumenty |
| `EventCoordinationController` | wydarzenia, harmonogramy, ofiary, uczestnicy, organizatorzy |
| `SacramentalMinistryController` | księża, sakramenty, udzielenia sakramentów |

Brak: **PUT**, **DELETE**, warstwa **serwisów**, **walidacja** (`@Valid`), **OpenAPI**.

### Testy

| Artefakt | Status |
|----------|--------|
| `EparishApplicationTests` | ✅ `contextLoads` (profil `test`) |
| `ApiIntegrationTest` | ✅ 9 scenariuszy zgodnych z Bruno |
| `application-test.properties` | ✅ H2 in-memory, `create-drop` |
| Kolekcja Bruno w `tests/` | ✅ 8 requestów (patrz §5) |

### Dokumentacja

| Plik | Status |
|------|--------|
| `README.md` | ✅ uruchomienie + API |
| `docs/docs.md` §2 Analiza | ✅ |
| `docs/docs.md` §3–5, §6.1 | ❌ puste — do uzupełnienia |
| `handoff.md` | ✅ ten plik |

---

## 4. Dane startowe (`data.sql`)

Po świeżym starcie dostępne m.in.:

| ID | Zasób |
|----|--------|
| 1 | diecezja, parafia, stanowiska 1–2, typy wydarzeń, harmonogram, **wydarzenie**, grupy, sakramenty, ksiądz, rodzina, parafianin |

Testy Bruno **Add Intention** / **Add Announcement** wymagają **`eventId: 1`**.  
**Register Sacrament** wymaga `parishionerId`, `priestId`, `sacramentId` = 1.  
**Add Employee** wymaga `parishId: 1`, `positionId: 1`.

---

## 5. Kolekcja Bruno (`tests/`)

| Folder | Plik | Metoda | Uwagi |
|--------|------|--------|--------|
| Event_Coordination | List Events | GET | **Uruchamiać pierwszy** — walidacja seeda |
| Event_Coordination | Add Intention | POST | `eventId: 1` |
| Event_Coordination | Add Announcement | POST | `eventId: 1` |
| Pastoral_Care | Add Family | POST | |
| Parish_Groups | Create Group | POST | |
| Staff_Management | Add Employee | POST | |
| Sacramental_Ministry | Register Sacrament | POST | ids = 1 |
| Parish_Information | List Parishes | GET | |

**Brakuje requestów Bruno** dla wielu nowych endpointów (memberships, offerings, documents, duties, POST events itd.) — do dopisania przez testera.

---

## 6. Mapowanie 5 przypadków użycia (szkic do dokumentacji)

Wymaganie projektu: **5 zaimplementowanych UC**. Propozycja powiązania z API (do wpisania w `docs/docs.md` §4):

| # | Przypadek użycia (biznes) | Endpoint(y) |
|---|---------------------------|-------------|
| 1 | Dodanie rodziny / opieka duszpasterska | `POST /api/families` |
| 2 | Utworzenie grupy parafialnej | `POST /api/groups` |
| 3 | Dodanie pracownika parafii | `POST /api/employees` |
| 4 | Dodanie intencji mszalnej | `POST /api/intentions` |
| 5 | Rejestracja sakramentu / ogłoszenie | `POST /api/sacrament-administrations` lub `POST /api/announcements` |

Zespół powinien **ustalić oficjalną listę 5 UC** z diagramów w `docs/docs.md` §2.3 i dopasować tabelę.

---

## 7. Co zostało do zrobienia (backlog)

### Wymagane na ocenę / prezentację

- [ ] **§3 Projekt** w `docs/docs.md` (diagramy DDD, ERD implementacyjny)
- [ ] **§4 Implementacja** (architektura, kontrolery, baza)
- [ ] **§5 Testy** (Bruno + JUnit, raport wyników)
- [ ] **§6.1 Role** (kto co robił)
- [ ] **`schema.sql`** (DDL) — osobny plik, np. `docs/sql/schema.sql`
- [ ] **Raport testów** (PDF/Markdown) + zrzuty Bruno
- [ ] Rozszerzyć **Bruno** o kluczowe brakujące endpointy
- [ ] **Suchy przebieg prezentacji** (świeża baza, kolejność requestów)

### Opcjonalne / jakość

- [ ] PUT/DELETE wybranych zasobów
- [ ] Warstwa serwisów + `@Valid`
- [ ] OpenAPI / Swagger UI
- [ ] Front (HTML lub Thymeleaf) — **tylko jeśli prowadzący wymaga**
- [ ] Usunąć / nie commitować `docs/jdk-26_windows-x64_bin.exe` (duży instalator)
- [ ] Lepsze generowanie ID (sekwencje zamiast `findAll().max()`)

### Znane ograniczenia techniczne

- ID generowane w kontrolerach przez `EntityIds.nextId()` — przy dużej bazie niewydajne.
- Baza plikowa H2 — przy `ddl-auto=update` + `data.sql` z `continue-on-error=true` stary plik `data/` może mieć niepełny seed → patrz §2.
- Brak `POST /api/parishes` — parafia tylko z seeda.
- Parafia w seedzie bez powiązanej miejscowości w `data.sql`.

---

## 8. Struktura repozytorium

```
eParafia/
├── src/main/java/edu/prz/eparish/
│   ├── EparishApplication.java
│   ├── api/                    # kontrolery REST
│   ├── duszpasterstwowiernych/
│   ├── grupyparafialne/
│   ├── informacjeoparafii/
│   ├── koordynacjawydarzen/
│   ├── organizacjarolizadan/
│   └── poslugasakramentalna/
├── src/main/resources/
│   ├── application.properties
│   └── data.sql
├── src/test/
│   ├── java/.../ApiIntegrationTest.java
│   └── resources/application-test.properties
├── tests/                      # kolekcja Bruno
├── docs/
│   ├── docs.md                 # dokumentacja projektu (główna)
│   └── wytyczne_do_testów.md
├── README.md
└── handoff.md                  # ten plik
```

---

## 9. Konfiguracja (`application.properties`)

| Właściwość | Znaczenie |
|------------|-----------|
| `spring.datasource.url` | `jdbc:h2:file:./data/eparish` |
| `spring.jpa.hibernate.ddl-auto` | `update` |
| `spring.sql.init.mode` | `always` — ładuje `data.sql` przy starcie |
| `spring.sql.init.continue-on-error` | `true` — przy istniejącej bazie duplikaty INSERTów są ignorowane |
| `spring.h2.console.enabled` | `true` |

Profil testowy: `src/test/resources/application-test.properties` (H2 in-memory, `create-drop`).

---

## 10. Kontakt / decyzje do podjęcia przez zespół

1. **Czy prowadzący wymaga frontu?** — domyślnie nie (patrz rozmowę w zespole / `docs.md`).
2. **Które dokładnie 5 UC wchodzi do raportu?** — doprecyzować z diagramów UML.
3. **Kto uzupełnia §3–§5 w `docs.md`?** — analityk/projektant/tester.
4. **Kto dopisuje Bruno i raport testów?** — tester.
5. **Kto robi `schema.sql`?** — programista (można wyeksportować z H2 po pierwszym uruchomieniu).

---

## 11. Przydatne komendy

```powershell
.\gradlew.bat build          # build + testy
.\gradlew.bat test           # tylko testy
.\gradlew.bat bootRun        # uruchomienie
.\gradlew.bat compileJava    # sama kompilacja
```

---

## 12. Historia zmian (skrót)

- Dodane pełne API REST (GET + POST), `HomeController`, trzeci/trzeci+ kontrolery domenowe.
- Naprawiony Whitelabel na `/`.
- `README.md` — instrukcja zespołu.
- `ApiIntegrationTest` + profil testowy.
- Poprawka kolumny `data_i_godzina` na encji wydarzenia.
- Bruno: `List Events`, test sakramentu, list parafii.

---

*Po przejęciu projektu: przeczytaj `README.md`, uruchom `bootRun`, odpal `gradlew test` i kolekcję Bruno. Pytania techniczne — patrz kod w `edu.prz.eparish.api`.*
