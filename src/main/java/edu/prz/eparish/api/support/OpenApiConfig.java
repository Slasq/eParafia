package edu.prz.eparish.api.support;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI eParishOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("eParafia API")
            .description("""
                ## System obsługi eParafia — REST API

                **Architektura:** Domain-Driven Design (DDD) — 6 kontekstów, 3 agregaty, warstwa serwisów i fabryk.

                ### Agregaty domenowe
                | Agregat | Korzeń | Endpoint |
                |---|---|---|
                | `WydarzenieAgregat` (złożony) | WydarzenieParafialne | `GET /api/events/{id}/aggregate` |
                | `ParafianinAgregat` | Parafianin | `GET /api/parishioners/{id}/aggregate` |
                | `GrupaParafialnaAgregat` | GrupaParafialna | `GET /api/groups/{id}/aggregate` |

                ### Przypadki użycia
                | UC | Kontekst | Serwis |
                |---|---|---|
                | Zarządzanie wydarzeniami parafialnymi | koordynacjawydarzen | EventCoordinationService |
                | Przypisanie intencji | koordynacjawydarzen | EventCoordinationService |
                | Zarządzanie ogłoszeniami parafialnymi | koordynacjawydarzen | EventCoordinationService |
                | Prowadzenie harmonogramu | koordynacjawydarzen | EventCoordinationService |
                | Prowadzenie ewidencji ofiar | koordynacjawydarzen | EventCoordinationService |
                | Przypisanie uczestników i organizatorów | koordynacjawydarzen | EventCoordinationService |
                | Zarządzanie parafią / diecezją / miejscowością | informacjeoparafii | ParishInformationService |
                | Zarządzanie parafianami | informacjeoparafii | ParishInformationService |
                | Prowadzenie kartotek parafian | informacjeoparafii | ParishInformationService |
                | Rejestracja zdarzeń religijnych | informacjeoparafii | ParishInformationService |
                | Zarządzanie dokumentacją | informacjeoparafii | ParishInformationService |
                | Zarządzanie wspólnotą / przypisanie do rodziny | duszpasterstwowiernych | PastoralCareService |
                | Dodanie / zmiana adresu rodziny | duszpasterstwowiernych | PastoralCareService |
                | Dodaj grupę / zmień grupę | grupyparafialne | ParishGroupService |
                | Dodaj członkostwo (z datami) | grupyparafialne | ParishGroupService |
                | Zarządzanie personelem parafii | organizacjarolizadan | ParishOperationsService |
                | Przydzielanie stanowiska | organizacjarolizadan | ParishOperationsService |
                | Przydzielanie obowiązku | organizacjarolizadan | ParishOperationsService |
                | Wykonanie obowiązku | organizacjarolizadan | ParishOperationsService |
                | Rejestrowanie sakramentów | poslugasakramentalna | SacramentalMinistryService |

                ### Fabryki
                `EventFactory`, `ParishInfoFactory`, `PastoralCareFactory`, `ParishGroupFactory`,
                `StaffFactory`, `SacramentalMinistryFactory`
                """)
            .version("2.0.0")
            .contact(new Contact()
                .name("Politechnika Rzeszowska — Inżynieria i Analiza Danych L2")));
  }
}
