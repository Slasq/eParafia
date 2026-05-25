# System obsługi eParafia — dokumentacja projektu

| | |
|---|---|
| **Projekt** | Usługi sieciowe w biznesie |
| **Temat** | System obsługi e parafii |
| **Kierunek / Grupa** | Inżynieria i analiza danych — L2 |
| **Uczelnia** | Politechnika Rzeszowska im. Ignacego Łukasiewicza |
| **Repozytorium** | https://github.com/Slasq/eParafia |
| **Data prezentacji** | 16/17.06.2026 |

---

## Organizacja projektu

1. Grupa projektowa jest zespołem deweloperskim pracującym zwinnie.
2. W zespole są reprezentowane poszczególne dyscypliny wytwórcze: analityk, projektant, programista, tester.
3. Produktem pracy zespołu jest działające i udokumentowane oprogramowanie.
4. **Produkty analityka:** wstępny opis wymagań, dokumentacja odkrywania encji, analityczny model dziedziny w postaci diagramu UML, wyjaśnienie modelu, dokumentacja odkrywania przypadków użycia, katalog przypadków użycia w postaci diagramu UML, opis przypadków użycia.
5. **Produkty projektanta:** model projektowy w stylu Domain-Driven Design zaprezentowany za pomocą diagramów UML, relacyjny model danych (UML lub ERD), inne diagramy UML (np. diagram sekwencji, diagram stanów).
6. **Produkty programisty:** kod programu Java/Spring/JPA/REST, skrypty DDL tworzące bazę danych.
7. **Produkty testera:** skrypty DML wypełniające bazę danymi testowymi, kolekcja testów (Bruno lub inne narzędzie), raport z testów.
8. Rozmiar modelu dziedziny: **20 encji**.
9. Liczba opisanych i zaimplementowanych przypadków użycia: **5**.
10. Dziedzinę (biznes) ustala zespół.
11. Wszystkie artefakty są składowane w repozytorium Git.

---

## Spis treści

1. [Wprowadzenie](#1-wprowadzenie)
2. [Analiza](#2-analiza)
   - 2.1 [Opis wymagań](#21-opis-wymagań)
   - 2.2 [Model dziedziny](#22-model-dziedziny)
     - 2.2.1 [Odkrywanie pojęć](#221-odkrywanie-pojęć)
     - 2.2.2 [Szkic modelu dziedziny](#222-szkic-modelu-dziedziny)
     - 2.2.3 [Wyjaśnienie modelu](#223-wyjaśnienie-modelu)
   - 2.3 [Model przypadków użycia](#23-model-przypadków-użycia)
     - 2.3.1 [Odkrywanie przypadków użycia i aktorów](#231-odkrywanie-przypadków-użycia-i-aktorów)
     - 2.3.2 [Diagram przypadków użycia](#232-diagram-przypadków-użycia)
     - 2.3.3 [Opis przypadków użycia](#233-opis-przypadków-użycia)
3. [Projekt](#3-projekt)
4. [Implementacja](#4-implementacja)
5. [Testy](#5-testy)
6. [Informacje dodatkowe](#6-informacje-dodatkowe)
   - 6.1 [Role w projekcie](#61-role-w-projekcie)

---

## 1 Wprowadzenie

System eParafia wspiera kompleksowe zarządzanie działalnością parafii. Jego głównym celem jest usprawnienie codziennej pracy administracyjnej oraz duszpasterskiej poprzez uporządkowanie i cyfryzację kluczowych procesów związanych z funkcjonowaniem wspólnoty parafialnej. Dzięki integracji wielu obszarów działalności w jednym systemie możliwe jest efektywne zarządzanie zarówno danymi wiernych, jak i wydarzeniami religijnymi, dokumentacją czy zasobami personalnymi.

---

## 2 Analiza

### 2.1 Opis wymagań

System eParafia umożliwia kompleksowe zarządzanie działalnością **parafii**, obejmując obsługę **wiernych, sakramentów, intencji mszalnych, wydarzeń** oraz administracji parafialnej.

System zawiera podstawowe informacje o **parafii** takie, jak jej nazwa, adres, telefon, email, datę erygowania oraz **miejscowość** i **diecezję**, do jakiej należy.

Podstawową funkcjonalnością systemu jest prowadzenie **kartotek parafian** oraz rejestracja **zdarzeń religijnych** związanych z ich życiem duchowym. Każdy **parafianin** posiada swoją kartotekę zawierającą dane osobowe, adresowe oraz kontaktowe.

System umożliwia rejestrowanie **sakramentów** udzielanych parafianom, takich jak chrzest, bierzmowanie, małżeństwo. Każdy sakrament zawiera informacje o dacie, miejscu, osobie udzielającej oraz powiązanych uczestnikach.

W systemie prowadzony jest **harmonogram** wydarzeń religijnych np. mszy świętych wraz z przypisanymi **intencjami**. Intencje mogą być zamawiane przez parafian i zawierają treść, datę realizacji oraz status realizacji.

Dodatkowo system umożliwia zarządzanie wydarzeniami parafialnymi, takimi jak rekolekcje, spotkania grup czy uroczystości religijne. Wydarzenia mogą mieć przypisanych **uczestników** oraz **organizatorów**.

System umożliwia zarządzanie **grupami parafialnymi** (np. ministranci, schola), do których przypisywani są parafianie.

W systemie każdy parafianin przypisany jest do konkretnej **rodziny**, a każda rodzina do konkretnego **adresu**.

System wspiera także zarządzanie personelem parafii (**księża, pracownicy świeccy**), ich rolami, **stanowiskami** oraz **obowiązkami**.

W ramach funkcjonalności administracyjnych system umożliwia prowadzenie ewidencji **ofiar** (datków), zarządzanie **ogłoszeniami parafialnymi** oraz **dokumentacją**.

### 2.2 Model dziedziny

#### 2.2.1 Odkrywanie pojęć

System obsługi e parafii składa się z następujących encji:

| Encja | Opis |
|---|---|
| **Parafia** | Parafia zawiera podstawowe informacje identyfikujące jednostkę, takie jak id_parafii, nazwa, adres, telefon, email oraz data_erygowania. |
| **Miejscowość** | Miejscowość określa miejsce, gdzie znajduje się parafia i zawiera informacje o nazwie, kodzie pocztowym oraz województwie, a także odniesienie do diecezji. |
| **Diecezja** | Diecezja zawiera informacje identyfikujące jednostkę, takie jak id_diecezji, nazwa, siedziba i biskup. |
| **Pracownik** | Pracownik zawiera imię i nazwisko pracownika zatrudnionego na konkretnym stanowisku oraz powiązania ze stanowiskiem i parafią. |
| **Stanowisko** | Stanowisko określa id, nazwę oraz opis funkcji. |
| **Obowiązek** | Obowiązek opisuje konkretne zadania przypisane do stanowiska poprzez nazwę i opis. |
| **Parafianin** | Parafianin przechowuje dane wiernych, takie jak id_parafianina, imię, nazwisko, PESEL, data urodzenia, telefon i email, a także powiązania z parafią i rodziną. |
| **Kartoteka** | Kartoteka zawiera id_kartoteki, datę utworzenia oraz opis. |
| **Dokument** | Dokument posiada id_dokumentu, typ, datę wystawienia, opis oraz powiązanie z kartoteką. |
| **Rodzina** | Rodzina zawiera id_rodziny, nazwisko rodziny oraz liczbę członków. Każdy parafianin należy do konkretnej rodziny. |
| **Adres_rodziny** | Rodzina posiada przypisany adres_rodziny, gdzie zapisywane są szczegóły lokalizacji, takie jak ulica, numer domu i mieszkania, kod pocztowy oraz miasto. |
| **Członkostwo** | Członkostwo określa okres przynależności (data_od_kiedy, data_do_kiedy) oraz powiązania z grupą i parafianinem. Poprzez tę encję parafianie mogą należeć do różnych grup. |
| **Grupa** | Grupa posiada id_grupy, nazwę, opis oraz opiekuna. |
| **Ksiądz** | Ksiądz posiada dane osobowe takie, jak imię, nazwisko, telefon, email, datę święceń, funkcji (proboszcz, wikariusz) oraz dane parafii, którą zarządza. |
| **Udzielanie_sakramentu** | Udzielanie_sakramentu łączy parafianina, księdza i sakrament, przechowując informacje o dacie udzielenia. |
| **Sakrament** | Sakrament zawiera id, nazwę i opis. |
| **Wydarzenie** | Wydarzenie przechowuje id, nazwę, datę, miejsce i opis oraz powiązania z parafią, typem wydarzenia i harmonogramem. |
| **Typ_wydarzenia** | Typ_wydarzenia definiuje kategorię wydarzenia poprzez id i nazwę. |
| **Harmonogram** | Harmonogram zawiera datę, godzinę oraz opis, porządkując przebieg wydarzeń. |
| **Intencja** | Intencja zawiera id_intencji, treść, datę oraz ofiarodawcę oraz powiązanie z konkretnym wydarzeniem. |
| **Ofiara** | Ofiara zawiera kwotę, datę oraz typ datku oraz powiązanie z konkretnym wydarzeniem. |
| **Ogłoszenie** | Ogłoszenie zawiera id oraz treść oraz powiązanie z konkretnym wydarzeniem. |
| **Organizator** | Organizator przechowuje dane osób (imię, nazwisko, rola) oraz powiązanie z konkretnym wydarzeniem. |
| **Uczestnik** | Uczestnik przechowuje dane osób (imię, nazwisko, rola) oraz powiązanie z konkretnym wydarzeniem. |

*Tabela 1. Opis encji*

#### 2.2.2 Szkic modelu dziedziny

![Rysunek 1. Diagram ERD](docs/img/rys1_diagram_erd.png)

*Rysunek 1. Diagram ERD*

![Rysunek 2. Diagram relacyjny](docs/img/rys2_diagram_relacyjny.png)

*Rysunek 2. Diagram relacyjny*

**Podział na konteksty:**

![Rysunek 3. Diagram relacyjny po podziale na konteksty](docs/img/rys3_diagram_konteksty.png)

*Rysunek 3. Diagram relacyjny po podziale na konteksty*

Gdzie:

1. **pomarańczowy** — dziedzina główna (posługa sakramentalna) — encje: Parafia, Parafianin, Ksiądz, Udzielanie sakramentu, Sakrament;
2. **czerwony** — informacje o parafii — encje: Parafia, Miejscowość, Diecezja, Ksiądz, Parafianin, Kartoteka, Dokument;
3. **fioletowy** — organizacja ról i zadań — encje: Pracownik, Stanowisko, Obowiązek;
4. **niebieski** — koordynacja wydarzeń — encje: Parafia, Ksiądz, Wydarzenie, Typ wydarzenia, Harmonogram, Intencja, Ofiara, Ogłoszenie, Uczestnik, Organizator;
5. **zielony** — duszpasterstwo wiernych — Parafianin, Rodzina, Adres rodziny;
6. **błękitny** — grupy parafialne — Parafianin, Członkostwo, Grupa.

**Dziedzina główna (posługa sakramentalna)**

![Dziedzina główna](docs/img/dziedzina_glowna.png)

**Dziedzina informacje o parafii**

![Dziedzina informacje o parafii](docs/img/dziedzina_informacje.png)

**Dziedzina organizacja ról i zadań**

![Dziedzina organizacja ról i zadań](docs/img/dziedzina_role.png)

**Dziedzina koordynacja wydarzeń**

![Dziedzina koordynacja wydarzeń](docs/img/dziedzina_wydarzenia.png)

**Dziedzina duszpasterstwo wiernych**

![Dziedzina duszpasterstwo wiernych](docs/img/dziedzina_duszpasterstwo.png)

**Dziedzina grupy parafialne**

![Dziedzina grupy parafialne](docs/img/dziedzina_grupy.png)

#### 2.2.3 Wyjaśnienie modelu

Pomiędzy poszczególnymi encjami występują następujące relacje:

| Relacja | Opis |
|---|---|
| Parafia – Pracownik (1:N) | Parafia ma jednego lub wielu pracowników, a konkretny pracownik jest przypisany do konkretnej parafii. |
| Pracownik – Stanowisko (1:N) | Konkretne stanowisko może mieć jednego lub więcej pracowników, a jeden pracownik jest przypisany do konkretnego stanowiska. |
| Stanowisko – Obowiązek (1:N) | Konkretne stanowisko może mieć wiele obowiązków do wykonania, a konkretny obowiązek jest przypisany do jednego stanowiska. |
| Parafia – Parafianin (1:N) | Parafia ma wielu parafian, a konkretny parafianin należy do konkretnej parafii. |
| Parafianin – Członkostwo (1:N) | Parafianin może należeć do zero, jednej lub wielu grup parafialnych, a konkretne członkostwo ma jednego parafianina. |
| Grupa – Członkostwo (1:N) | Grupa może mieć wiele członkostw, a konkretne członkostwo należy do jednej grupy. |
| Rodzina – Parafianin (1:N) | Konkretna rodzina może mieć wielu parafian, a konkretny parafianin należy do jednej rodziny. |
| Adres_rodziny – Rodzina (1:1) | Konkretna rodzina ma jeden konkretny adres, a konkretny adres jest przypisany do jednej konkretnej rodziny. |
| Kartoteka – Parafianin (1:1) | Konkretna kartoteka należy do jednego parafianina, a konkretny parafianin ma jedną konkretną kartotekę. |
| Kartoteka – Dokument (1:N) | Konkretna kartoteka może mieć wiele dokumentów, a konkretny dokument należy do jednej konkretnej kartoteki. |
| Parafia – Miejscowość (1:1) | Konkretna parafia znajduje się w konkretnej miejscowości, a konkretna miejscowość ma konkretną parafię. |
| Diecezja – Miejscowość (1:N) | Konkretna diecezja może zawierać wiele miejscowości, a konkretna miejscowość należy do jednej konkretnej diecezji. |
| Parafia – Ksiądz (1:N) | Konkretna parafia może mieć wielu księży, a konkretny ksiądz należy do jednej, konkretnej parafii. |
| Ksiądz – Udzielanie sakramentu (1:N) | Konkretny ksiądz może udzielać wielu sakramentów, a konkretny sakrament jest udzielany przez jednego konkretnego księdza. |
| Sakrament – Udzielanie sakramentu (1:N) | Konkretny sakrament może być udzielany wiele razy, a konkretne udzielenie sakramentu dotyczy jednego konkretnego sakramentu. |
| Parafia – Wydarzenie (1:N) | Konkretna parafia może mieć wiele wydarzeń, a konkretne wydarzenie dotyczy konkretnej parafii. |
| Typ_wydarzenia – Wydarzenie (1:N) | Konkretny typ wydarzenia może być przypisany do wielu wydarzeń, a konkretne wydarzenie to jeden konkretny typ wydarzenia (np. msza, rekolekcje). |
| Harmonogram – Wydarzenie (1:N) | Konkretny harmonogram może być w ramach wielu wydarzeń, a konkretne wydarzenie ma jeden konkretny harmonogram. |
| Wydarzenie – Intencja (1:N) | Konkretne wydarzenie może być odprawiane w ramach wielu intencji, a konkretna intencja dotyczy jednego konkretnego wydarzenia. |
| Wydarzenie – Ofiara (1:N) | Konkretne wydarzenie może mieć zbieranych wiele ofiar, a konkretna ofiara dotyczy konkretnego wydarzenia. |
| Wydarzenie – Ogłoszenie (1:N) | Konkretne wydarzenie może mieć wiele ogłoszeń, a konkretne ogłoszenie dotyczy konkretnego wydarzenia. |
| Wydarzenie – Organizator (1:N) | Konkretne wydarzenie może mieć wielu organizatorów, a konkretny organizator jest przypisany do jednego konkretnego wydarzenia. |
| Wydarzenie – Uczestnik (1:N) | Konkretne wydarzenie może mieć wielu uczestników, a konkretny uczestnik jest przypisany do jednego konkretnego wydarzenia. |

*Tabela 2. Opis relacji*

### 2.3 Model przypadków użycia

#### 2.3.1 Odkrywanie przypadków użycia i aktorów

Dla systemu wspomagającego pracę parafii zostało zaprojektowanych i stworzonych sześć diagramów przypadków użycia.

#### 2.3.2 Diagram przypadków użycia

**Diagram przypadków użycia — organizowanie wydarzeń parafialnych**

![Rysunek 4. Diagram przypadków użycia - organizowanie wydarzeń parafialnych](docs/img/rys4_uc_wydarzenia.png)

*Rysunek 4. Diagram przypadków użycia — organizowanie wydarzeń parafialnych*

**Diagram przypadków użycia — zarządzanie obowiązkami parafialnymi**

![Rysunek 5. Diagram przypadków użycia – zarządzanie obowiązkami parafialnymi](docs/img/rys5_uc_obowiazki.png)

*Rysunek 5. Diagram przypadków użycia — zarządzanie obowiązkami parafialnymi*

**Diagram przypadków użycia — zarządzanie parafianami**

![Rysunek 6. Diagram przypadków użycia – zarządzanie parafianami](docs/img/rys6_uc_parafianie.png)

*Rysunek 6. Diagram przypadków użycia — zarządzanie parafianami*

**Diagram przypadków użycia — zarządzanie parafią**

![Rysunek 7. Diagram przypadków użycia – zarządzanie parafią](docs/img/rys7_uc_parafia.png)

*Rysunek 7. Diagram przypadków użycia — zarządzanie parafią*

**Diagram przypadków użycia — zarządzanie wspólnotą parafialną**

![Rysunek 8. Diagram przypadków użycia - zarządzanie wspólnotą parafialną](docs/img/rys8_uc_wspolnota.png)

*Rysunek 8. Diagram przypadków użycia — zarządzanie wspólnotą parafialną*

**Diagram przypadków użycia — zarządzanie grupami parafialnymi**

![Rysunek 9. Diagram przypadków użycia - zarządzanie grupami parafialnymi](docs/img/rys9_uc_grupy.png)

*Rysunek 9. Diagram przypadków użycia — zarządzanie grupami parafialnymi*

#### 2.3.3 Opis przypadków użycia

**Diagram przypadków użycia — organizowanie wydarzeń parafialnych**

Diagram przedstawia przypadki użycia systemu wspierającego zarządzanie wydarzeniami parafialnymi. W systemie wyróżniono dwóch aktorów: **Organizator** oraz **Ksiądz**, którzy korzystają z różnych funkcjonalności.

Głównym przypadkiem użycia jest **zarządzanie wydarzeniami parafialnymi**, z którego korzysta Organizator. Funkcja ta obejmuje kompleksową obsługę wydarzeń odbywających się w parafii. W jej ramach wyróżniono trzy rozszerzenia (relacje «extends»), które reprezentują dodatkowe, opcjonalne funkcjonalności:

- przypisanie intencji,
- zarządzanie ogłoszeniami parafialnymi,
- prowadzenie harmonogramu.

Oznacza to, że podczas zarządzania wydarzeniami Organizator może skorzystać z tych funkcji w zależności od potrzeb.

Drugim przypadkiem użycia jest **przypisanie uczestników oraz organizatorów**, z którego korzysta aktor Ksiądz. Funkcjonalność ta umożliwia zarządzanie osobami zaangażowanymi w wydarzenia parafialne, w tym przypisywanie odpowiednich ról.

System został zaprojektowany w sposób modularny, co pozwala na elastyczne rozszerzanie jego funkcjonalności oraz dostosowanie do różnych potrzeb organizacyjnych parafii.

---

**Diagram przypadków użycia — zarządzanie obowiązkami parafialnymi**

Diagram przedstawia przypadki użycia systemu odpowiedzialnego za zarządzanie personelem parafii. Wyróżniono dwóch aktorów: **Ksiądz** oraz **Pracownik**, którzy korzystają z różnych funkcji systemu.

Głównym przypadkiem użycia jest **zarządzanie personelem parafii**, realizowane przez aktora Księdza. Funkcjonalność ta umożliwia kompleksowe administrowanie personelem, w tym przypisywanie ról i obowiązków. W ramach tego przypadku użycia wyróżniono trzy relacje rozszerzenia («extends»), które reprezentują dodatkowe operacje:

- przydzielanie stanowiska,
- przydzielanie parafii,
- przydzielanie obowiązku.

Relacje te wskazują, że poszczególne działania mogą być wykonywane opcjonalnie w zależności od aktualnych potrzeb zarządzania personelem.

Drugim przypadkiem użycia jest **wykonanie obowiązku**, z którego korzysta aktor Pracownik. Funkcja ta pozwala na realizację przypisanych zadań przez członków personelu, co stanowi końcowy etap procesu zarządzania obowiązkami.

Diagram odzwierciedla hierarchiczną strukturę zarządzania, gdzie Ksiądz pełni rolę administratora, a Pracownik realizuje powierzone zadania. Takie podejście umożliwia przejrzyste rozdzielenie odpowiedzialności w systemie.

---

**Diagram przypadków użycia — zarządzanie parafianami**

Diagram przedstawia funkcjonalności systemu związane z zarządzaniem działalnością parafii. Wyróżniono dwóch aktorów: **Ksiądz** oraz **Parafianin**, którzy korzystają z systemu w różnym zakresie.

Głównym przypadkiem użycia jest **zarządzanie działalnością parafii**, realizowane przez aktora Księdza. Funkcjonalność ta obejmuje szeroki zakres działań administracyjnych i organizacyjnych związanych z funkcjonowaniem parafii. W jej ramach wyróżniono kilka relacji rozszerzenia («extends»), które reprezentują szczegółowe operacje:

- prowadzenie kartotek parafian,
- rejestracja zdarzeń religijnych,
- prowadzenie ewidencji ofiar,
- zarządzanie dokumentacją.

Każda z tych funkcji może być wykonywana w zależności od aktualnych potrzeb, co wskazuje na elastyczność systemu i jego modułową budowę.

Dodatkowo w diagramie uwzględniono przypadek użycia **rejestrowanie sakramentów**, który jest powiązany z główną funkcjonalnością poprzez relację «extends». Oznacza to, że proces ten stanowi rozszerzenie zarządzania działalnością parafii i może być realizowany w określonych sytuacjach.

Aktor Parafianin korzysta z funkcji rejestrowania sakramentów, co wskazuje na jego udział w procesach religijnych obsługiwanych przez system. Ksiądz natomiast pełni rolę administratora i nadzoruje całość działalności parafii.

Diagram obrazuje kompleksowe podejście do zarządzania parafią, integrując zarówno aspekty administracyjne, jak i religijne w jednym systemie.

---

**Diagram przypadków użycia — zarządzanie parafią**

Diagram przedstawia przypadki użycia systemu wspierającego zarządzanie parafią. W systemie wyróżniono dwóch aktorów: **Ksiądz** oraz **Parafianin**, którzy korzystają z różnych funkcjonalności systemu.

Głównym przypadkiem użycia jest **zarządzanie parafią**, realizowane przez aktora Księdza. Funkcjonalność ta umożliwia administrowanie podstawowymi danymi parafii oraz jej strukturą administracyjną. W ramach tego przypadku użycia występuje relacja włączenia («includes») do:

- dodaj miejscowość,

która z kolei zawiera relację «includes» do:

- dodaj diecezję.

Oznacza to, że w procesie zarządzania parafią wymagane jest określenie miejscowości oraz powiązanej z nią diecezji.

Przypadek użycia **dodaj diecezję** posiada dodatkowe relacje rozszerzenia («extends»), które reprezentują opcjonalne funkcjonalności:

- dodaj biskupa,
- dodaj siedzibę,
- zmień nazwę.

Relacje te wskazują, że podczas dodawania diecezji możliwe jest uzupełnienie jej danych o dodatkowe informacje w zależności od potrzeb.

Drugim obszarem funkcjonalnym systemu jest zarządzanie kartoteką parafianina, z którego korzysta aktor Parafianin. Głównym przypadkiem użycia jest:

- dodaj kartotekę.

Przypadek ten posiada relację rozszerzenia («extends») do:

- dodaj dokument,

co oznacza, że dodanie dokumentu stanowi opcjonalne rozszerzenie procesu tworzenia kartoteki.

Przypadek użycia **dodaj dokument** zawiera relację włączenia («includes») do:

- dodaj typ,

oraz relacje rozszerzenia («extends») do dodatkowych operacji:

- dodaj opis,
- dodaj datę wystawienia.

Oznacza to, że określenie typu dokumentu jest wymagane, natomiast pozostałe informacje mogą być uzupełniane opcjonalnie.

Diagram przedstawia modularną strukturę systemu, w której główne funkcjonalności mogą być rozwijane o dodatkowe operacje. Takie podejście zapewnia elastyczność w zarządzaniu danymi parafii oraz kartotekami parafian, a także umożliwia łatwe dostosowanie systemu do zmieniających się potrzeb.

---

**Diagram przypadków użycia — zarządzanie wspólnotą parafialną**

Diagram przedstawia przypadki użycia systemu wspierającego **zarządzanie wspólnotą parafialną**. W systemie wyróżniono dwóch aktorów: **Parafianin** oraz **Rodzina**, którzy korzystają z dostępnych funkcjonalności systemu.

Głównym przypadkiem użycia realizowanym przez aktora Parafianina jest:

- przypisanie do rodziny.

Funkcjonalność ta umożliwia powiązanie parafianina z wybraną rodziną w systemie. Przypadek ten posiada relację rozszerzenia («extends») do:

- zmiana nazwiska rodziny.

Oznacza to, że w trakcie przypisywania parafianina do rodziny istnieje możliwość opcjonalnej zmiany nazwiska rodziny, jeśli zaistnieje taka potrzeba.

Drugim obszarem funkcjonalnym systemu jest zarządzanie rodziną, realizowane przez aktora Rodzina. Głównym przypadkiem użycia jest:

- dodanie członka rodziny.

Funkcjonalność ta umożliwia rozszerzenie składu rodziny o nowych członków. Przypadek ten zawiera relację włączenia («includes») do:

- dodanie adresu,

co oznacza, że podczas dodawania członka rodziny wymagane jest określenie adresu.

Przypadek użycia **dodanie adresu** posiada relację rozszerzenia («extends») do:

- zmiana adresu.

Relacja ta wskazuje, że po dodaniu adresu istnieje możliwość jego późniejszej modyfikacji.

Dodatkowym przypadkiem użycia realizowanym przez aktora Rodzina jest:

- dodaj liczbę członków rodziny.

Funkcjonalność ta pozwala na określenie liczby członków należących do danej rodziny.

Diagram przedstawia podstawowe operacje związane z zarządzaniem wspólnotą parafialną, obejmujące zarówno przypisywanie parafian do rodzin, jak i zarządzanie strukturą oraz danymi rodzin. Modularna budowa systemu umożliwia jego dalszą rozbudowę oraz dostosowanie do zmieniających się potrzeb.

---

**Diagram przypadków użycia — zarządzanie grupami parafialnymi**

Diagram przedstawia przypadki użycia systemu wspierającego **zarządzanie grupami parafialnymi**. W systemie wyróżniono dwóch aktorów: **Grupa** oraz **Parafianin**, którzy korzystają z dostępnych funkcjonalności.

Głównym przypadkiem użycia realizowanym przez aktora Grupa jest:

- dodaj członkostwo.

Funkcjonalność ta umożliwia przypisanie parafianina do wybranej grupy parafialnej. Przypadek ten posiada dwie relacje rozszerzenia («extends»), które reprezentują opcjonalne operacje:

- dodaj datę rozpoczęcia,
- dodaj datę zakończenia.

Oznacza to, że podczas dodawania członkostwa można dodatkowo określić okres przynależności parafianina do grupy, jednak nie jest to wymagane.

Drugim obszarem funkcjonalnym systemu jest zarządzanie samymi grupami, realizowane przez aktora Parafianin. Głównym przypadkiem użycia jest:

- dodaj grupę.

Funkcjonalność ta umożliwia utworzenie nowej grupy parafialnej w systemie. Przypadek ten posiada relację rozszerzenia («extends») do:

- zmień grupę.

Relacja ta wskazuje, że po utworzeniu grupy istnieje możliwość jej późniejszej modyfikacji.

Diagram przedstawia podstawowe operacje związane z zarządzaniem grupami parafialnymi, obejmujące zarówno przypisywanie członków do grup, jak i tworzenie oraz edycję grup. Modularna struktura systemu pozwala na jego dalszą rozbudowę oraz elastyczne dostosowanie do potrzeb organizacyjnych parafii.

---

## 3 Projekt

*Sekcja do uzupełnienia.*

---

## 4 Implementacja

*Sekcja do uzupełnienia.*

---

## 5 Testy

*Sekcja do uzupełnienia.*

---

## 6 Informacje dodatkowe

### 6.1 Role w projekcie

*Sekcja do uzupełnienia.*
