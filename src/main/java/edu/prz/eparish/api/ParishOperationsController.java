package edu.prz.eparish.api;

import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.informacjeoparafii.domain.parafia.ParafiaRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.intencja.Intencja;
import edu.prz.eparish.koordynacjawydarzen.domain.intencja.IntencjaRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.ogloszenie.Ogloszenie;
import edu.prz.eparish.koordynacjawydarzen.domain.ogloszenie.OgloszenieRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialne;
import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialneRepozytorium;
import edu.prz.eparish.organizacjarolizadan.domain.pracownik.Pracownik;
import edu.prz.eparish.organizacjarolizadan.domain.pracownik.PracownikRepozytorium;
import edu.prz.eparish.organizacjarolizadan.domain.stanowisko.Stanowisko;
import edu.prz.eparish.organizacjarolizadan.domain.stanowisko.StanowiskoRepozytorium;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ParishOperationsController {

  private final PracownikRepozytorium pracownikRepozytorium;
  private final ParafiaRepozytorium parafiaRepozytorium;
  private final StanowiskoRepozytorium stanowiskoRepozytorium;
  private final IntencjaRepozytorium intencjaRepozytorium;
  private final OgloszenieRepozytorium ogloszenieRepozytorium;
  private final WydarzenieParafialneRepozytorium wydarzenieParafialneRepozytorium;

  @PostMapping("/employees")
  public ResponseEntity<EmployeeResponse> addEmployee(@RequestBody AddEmployeeRequest request) {
    Parafia parafia = parafiaRepozytorium.findById(request.parishId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parafia nie istnieje"));
    Stanowisko stanowisko = stanowiskoRepozytorium.findById(request.positionId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stanowisko nie istnieje"));

    Pracownik pracownik = new Pracownik();
    pracownik.setId(nextEmployeeId());
    pracownik.setImie(request.firstName());
    pracownik.setNazwisko(request.lastName());
    pracownik.setParafia(parafia);
    pracownik.setStanowisko(stanowisko);

    Pracownik saved = pracownikRepozytorium.save(pracownik);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new EmployeeResponse(
            saved.getId(),
            saved.getImie(),
            saved.getNazwisko(),
            parafia.getId(),
            stanowisko.getId()));
  }

  @PostMapping("/intentions")
  public ResponseEntity<IntentionResponse> addIntention(@RequestBody AddIntentionRequest request) {
    WydarzenieParafialne wydarzenie = wydarzenieParafialneRepozytorium.findById(request.eventId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wydarzenie nie istnieje"));

    Intencja intencja = new Intencja();
    intencja.setId(nextIntentionId());
    intencja.setTresc(request.content());
    intencja.setData(request.date());
    intencja.setOfiarodawca(request.donor());
    intencja.setStatus("ZAPLANOWANA");
    intencja.setWydarzenie(wydarzenie);

    Intencja saved = intencjaRepozytorium.save(intencja);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new IntentionResponse(
            saved.getId(),
            saved.getTresc(),
            saved.getData(),
            saved.getOfiarodawca(),
            wydarzenie.getId()));
  }

  @PostMapping("/announcements")
  public ResponseEntity<AnnouncementResponse> addAnnouncement(@RequestBody AddAnnouncementRequest request) {
    WydarzenieParafialne wydarzenie = wydarzenieParafialneRepozytorium.findById(request.eventId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wydarzenie nie istnieje"));

    Ogloszenie ogloszenie = new Ogloszenie();
    ogloszenie.setId(nextAnnouncementId());
    ogloszenie.setTresc(request.content());
    ogloszenie.setWydarzenie(wydarzenie);

    Ogloszenie saved = ogloszenieRepozytorium.save(ogloszenie);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new AnnouncementResponse(saved.getId(), saved.getTresc(), wydarzenie.getId()));
  }

  private Long nextEmployeeId() {
    return pracownikRepozytorium.findAll().stream()
        .map(Pracownik::getId)
        .max(Long::compareTo)
        .orElse(0L) + 1;
  }

  private Long nextIntentionId() {
    return intencjaRepozytorium.findAll().stream()
        .map(Intencja::getId)
        .max(Long::compareTo)
        .orElse(0L) + 1;
  }

  private Long nextAnnouncementId() {
    return ogloszenieRepozytorium.findAll().stream()
        .map(Ogloszenie::getId)
        .max(Long::compareTo)
        .orElse(0L) + 1;
  }

  public record AddEmployeeRequest(String firstName, String lastName, Long parishId, Long positionId) {}

  public record EmployeeResponse(Long id, String firstName, String lastName, Long parishId, Long positionId) {}

  public record AddIntentionRequest(String content, LocalDate date, String donor, Long eventId) {}

  public record IntentionResponse(Long id, String content, LocalDate date, String donor, Long eventId) {}

  public record AddAnnouncementRequest(String content, Long eventId) {}

  public record AnnouncementResponse(Long id, String content, Long eventId) {}
}
