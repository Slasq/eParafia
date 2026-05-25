# Raport z testów integracyjnych API

## Weryfikowane metody kontrolerów
Poniżej przedstawiono kody źródłowe scenariuszy testowych, które posłużyły do weryfikacji poprawności działania dodanych endpointów.

### Metody typu POST (Tworzenie zasobów)
Weryfikacja polegała na przesłaniu poprawnego obiektu JSON i oczekiwaniu na odpowiedź serwera ze statusem HTTP `201 Created`. Dla relacyjnych i złożonych obiektów sprawdzano również, czy odpowiedź zwrotna zawiera wygenerowany klucz główny (ID).

**Rejestracja sakramentów, pracowników, rodzin i grup:**
```javascript
test("Status code is 201", function() {
    expect(res.getStatus()).to.equal(201);
});
```

**Dodawanie pracownika**
```javascript
test("Status code is 201", function() {
    expect(res.getStatus()).to.equal(201);
});
```

**Dodawanie rodziny**
```javascript
test("Status code is 201", function() {
    expect(res.getStatus()).to.equal(201);
});
```
**Tworzenie grupy**
```javascript
test("Status code is 201", function() {
    expect(res.getStatus()).to.equal(201);
});
```
**Dodawanie intencji**
```javascript
test("Status code is 201", function() {
    expect(res.getStatus()).to.equal(201);
});
test("Response contains intention id", function() {
    const body = res.getBody();
    expect(body).to.have.property("id");
    expect(body.eventId).to.equal(1);
});
```

**Dodawanie ogłoszeń**
```javascript
test("Status code is 201", function() {
    expect(res.getStatus()).to.equal(201);
});
test("Response contains announcement id", function() {
    const body = res.getBody();
    expect(body).to.have.property("id");
    expect(body.eventId).to.equal(1);
});
```


### Metody typu GET (Pobieranie zasobów)
Weryfikacja metod typu GET polegała na wywołaniu odpowiednich endpointów i sprawdzeniu, czy serwer prawidłowo odczytuje oraz zwraca żądane dane. Podstawowym kryterium zaliczenia testu było otrzymanie od API statusu HTTP `200 OK`. Dodatkowo, dla wybranych zasobów upewniono się, że struktura odpowiedzi zawiera odpowiednie dane startowe, co potwierdza poprawną inicjalizację i integralność bazy danych.

**Pobieranie listy parafii**
```javascript
test("Status code is 200", function() {
    expect(res.getStatus()).to.equal(200);
});
```

**Pobieranie listy wydarzeń**
```javascript
test("Status code is 200", function() {
    expect(res.getStatus()).to.equal(200);
});
test("Seed event with id 1 exists (required for Add Intention / Add Announcement)", function() {
    const events = res.getBody();
    const hasSeedEvent = Array.isArray(events) && events.some(e => e.id === 1);
    expect(hasSeedEvent, "Brak wydarzenia id=1. Zrestartuj aplikacje z swieza baza (usun folder data/ w projekcie).").to.be.true;
});
```


## Przeprowadzenie testów integracyjnych i wyniki
Testy automatyczne (backendowe) zostały pomyślnie uruchomione i zweryfikowane za pomocą narzędzia budującego Gradle przy użyciu komendy środowiskowej .\gradlew.bat test. Proces ten potwierdził poprawność przesyłanych struktur JSON oraz bezbłędną komunikację z lokalną bazą danych (H2).

![alt text](<images/Wynik_testow_gradle.png>)

Analogiczne scenariusze testowe zostały przeprowadzone od strony klienta API przy użyciu narzędzia Bruno. 

![alt text](<images/Wynik_testow_bruno.png>)