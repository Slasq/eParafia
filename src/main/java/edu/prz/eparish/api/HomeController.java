package edu.prz.eparish.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

  @GetMapping("/")
  public Map<String, Object> home() {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("application", "eparish");
    body.put("status", "running");
    body.put("apiBase", "/api");
    body.put("swaggerUi", "/swagger-ui/index.html");
    body.put("h2Console", "/h2-console");
    body.put("aggregates", List.of(
        "GET /api/events/{id}/aggregate      → WydarzenieAgregat (complex)",
        "GET /api/parishioners/{id}/aggregate → ParafianinAgregat",
        "GET /api/groups/{id}/aggregate       → GrupaParafialnaAgregat"));
    body.put("useCases", List.of(
        "POST /api/events                     UC: zarządzanie wydarzeniami",
        "POST /api/events/{id}/intentions     UC: przypisanie intencji",
        "PUT  /api/events/{eId}/intentions/{iId}/realize  UC: prowadzenie harmonogramu",
        "POST /api/events/{id}/announcements  UC: zarządzanie ogłoszeniami",
        "POST /api/events/{id}/offerings      UC: ewidencja ofiar",
        "POST /api/events/{id}/participants   UC: przypisanie uczestników",
        "POST /api/events/{id}/organizers     UC: przypisanie organizatorów",
        "POST /api/parishes                   UC: zarządzanie parafią",
        "POST /api/parishioners               UC: zarządzanie parafianami",
        "POST /api/parishioners/{id}/record   UC: prowadzenie kartotek",
        "PUT  /api/records/{id}               UC: rejestracja zdarzeń religijnych",
        "POST /api/parishioners/{id}/record/documents  UC: zarządzanie dokumentacją",
        "POST /api/families                   UC: zarządzanie wspólnotą",
        "PUT  /api/parishioners/{pid}/family/{fid}  UC: przypisanie do rodziny",
        "POST /api/family-addresses           UC: dodanie adresu",
        "PUT  /api/family-addresses/{id}      UC: zmiana adresu",
        "POST /api/groups                     UC: dodaj grupę",
        "PUT  /api/groups/{id}                UC: zmień grupę",
        "POST /api/memberships                UC: dodaj członkostwo",
        "PUT  /api/memberships/{id}/terminate UC: zakończ członkostwo",
        "POST /api/employees                  UC: zarządzanie personelem",
        "POST /api/positions                  UC: przydzielanie stanowiska",
        "POST /api/duties                     UC: przydzielanie obowiązku",
        "PUT  /api/duties/{id}/complete       UC: wykonanie obowiązku",
        "POST /api/sacrament-administrations  UC: rejestrowanie sakramentów"));
    return body;
  }
}
