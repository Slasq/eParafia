package edu.prz.eparish.api;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.informacjeoparafii.domain.parafia.ParafiaRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.intencja.Intencja;
import edu.prz.eparish.koordynacjawydarzen.domain.intencja.IntencjaRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.ogloszenie.Ogloszenie;
import edu.prz.eparish.koordynacjawydarzen.domain.ogloszenie.OgloszenieRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialne;
import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialneRepozytorium;
import edu.prz.eparish.organizacjarolizadan.domain.obowiazek.Obowiazek;
import edu.prz.eparish.organizacjarolizadan.domain.obowiazek.ObowiazekRepozytorium;
import edu.prz.eparish.organizacjarolizadan.domain.pracownik.Pracownik;
import edu.prz.eparish.organizacjarolizadan.domain.pracownik.PracownikRepozytorium;
import edu.prz.eparish.organizacjarolizadan.domain.stanowisko.Stanowisko;
import edu.prz.eparish.organizacjarolizadan.domain.stanowisko.StanowiskoRepozytorium;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  private final ObowiazekRepozytorium obowiazekRepozytorium;
  private final IntencjaRepozytorium intencjaRepozytorium;
  private final OgloszenieRepozytorium ogloszenieRepozytorium;
  private final WydarzenieParafialneRepozytorium wydarzenieParafialneRepozytorium;

  @GetMapping("/employees")
  public List<EmployeeResponse> listEmployees() {
    return pracownikRepozytorium.findAll().stream().map(this::toEmployeeResponse).toList();
  }

  @GetMapping("/employees/{id}")
  public EmployeeResponse getEmployee(@PathVariable Long id) {
    return toEmployeeResponse(findEmployee(id));
  }

  @PostMapping("/employees")
  public ResponseEntity<EmployeeResponse> addEmployee(@RequestBody AddEmployeeRequest request) {
    Parafia parafia = parafiaRepozytorium.findById(request.parishId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parafia nie istnieje"));
    Stanowisko stanowisko = stanowiskoRepozytorium.findById(request.positionId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stanowisko nie istnieje"));

    Pracownik pracownik = new Pracownik();
    pracownik.setId(EntityIds.nextId(pracownikRepozytorium, Pracownik::getId));
    pracownik.setImie(request.firstName());
    pracownik.setNazwisko(request.lastName());
    pracownik.setParafia(parafia);
    pracownik.setStanowisko(stanowisko);

    Pracownik saved = pracownikRepozytorium.save(pracownik);
    return ResponseEntity.status(HttpStatus.CREATED).body(toEmployeeResponse(saved));
  }

  @GetMapping("/positions")
  public List<PositionResponse> listPositions() {
    return stanowiskoRepozytorium.findAll().stream()
        .map(s -> new PositionResponse(s.getId(), s.getNazwa(), s.getOpis()))
        .toList();
  }

  @GetMapping("/duties")
  public List<DutyResponse> listDuties() {
    return obowiazekRepozytorium.findAll().stream().map(this::toDutyResponse).toList();
  }

  @PostMapping("/duties")
  public ResponseEntity<DutyResponse> addDuty(@RequestBody AddDutyRequest request) {
    Stanowisko stanowisko = stanowiskoRepozytorium.findById(request.positionId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stanowisko nie istnieje"));

    Obowiazek obowiazek = new Obowiazek();
    obowiazek.setId(EntityIds.nextId(obowiazekRepozytorium, Obowiazek::getId));
    obowiazek.setNazwa(request.name());
    obowiazek.setOpis(request.description());
    obowiazek.setStanowisko(stanowisko);

    Obowiazek saved = obowiazekRepozytorium.save(obowiazek);
    return ResponseEntity.status(HttpStatus.CREATED).body(toDutyResponse(saved));
  }

  @GetMapping("/intentions")
  public List<IntentionResponse> listIntentions() {
    return intencjaRepozytorium.findAll().stream().map(this::toIntentionResponse).toList();
  }

  @GetMapping("/intentions/{id}")
  public IntentionResponse getIntention(@PathVariable Long id) {
    return toIntentionResponse(findIntention(id));
  }

  @PostMapping("/intentions")
  public ResponseEntity<IntentionResponse> addIntention(@RequestBody AddIntentionRequest request) {
    WydarzenieParafialne wydarzenie = wydarzenieParafialneRepozytorium.findById(request.eventId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wydarzenie nie istnieje"));

    Intencja intencja = new Intencja();
    intencja.setId(EntityIds.nextId(intencjaRepozytorium, Intencja::getId));
    intencja.setTresc(request.content());
    intencja.setData(request.date());
    intencja.setOfiarodawca(request.donor());
    intencja.setStatus("ZAPLANOWANA");
    intencja.setWydarzenie(wydarzenie);

    Intencja saved = intencjaRepozytorium.save(intencja);
    return ResponseEntity.status(HttpStatus.CREATED).body(toIntentionResponse(saved));
  }

  @GetMapping("/announcements")
  public List<AnnouncementResponse> listAnnouncements() {
    return ogloszenieRepozytorium.findAll().stream().map(this::toAnnouncementResponse).toList();
  }

  @GetMapping("/announcements/{id}")
  public AnnouncementResponse getAnnouncement(@PathVariable Long id) {
    return toAnnouncementResponse(findAnnouncement(id));
  }

  @PostMapping("/announcements")
  public ResponseEntity<AnnouncementResponse> addAnnouncement(@RequestBody AddAnnouncementRequest request) {
    WydarzenieParafialne wydarzenie = wydarzenieParafialneRepozytorium.findById(request.eventId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wydarzenie nie istnieje"));

    Ogloszenie ogloszenie = new Ogloszenie();
    ogloszenie.setId(EntityIds.nextId(ogloszenieRepozytorium, Ogloszenie::getId));
    ogloszenie.setTresc(request.content());
    ogloszenie.setWydarzenie(wydarzenie);

    Ogloszenie saved = ogloszenieRepozytorium.save(ogloszenie);
    return ResponseEntity.status(HttpStatus.CREATED).body(toAnnouncementResponse(saved));
  }

  private Pracownik findEmployee(Long id) {
    return pracownikRepozytorium.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pracownik nie istnieje"));
  }

  private Intencja findIntention(Long id) {
    return intencjaRepozytorium.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Intencja nie istnieje"));
  }

  private Ogloszenie findAnnouncement(Long id) {
    return ogloszenieRepozytorium.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ogloszenie nie istnieje"));
  }

  private EmployeeResponse toEmployeeResponse(Pracownik saved) {
    return new EmployeeResponse(
        saved.getId(),
        saved.getImie(),
        saved.getNazwisko(),
        saved.getParafia().getId(),
        saved.getStanowisko().getId());
  }

  private DutyResponse toDutyResponse(Obowiazek o) {
    return new DutyResponse(o.getId(), o.getNazwa(), o.getOpis(), o.getStanowisko().getId());
  }

  private IntentionResponse toIntentionResponse(Intencja saved) {
    return new IntentionResponse(
        saved.getId(),
        saved.getTresc(),
        saved.getData(),
        saved.getOfiarodawca(),
        saved.getWydarzenie().getId());
  }

  private AnnouncementResponse toAnnouncementResponse(Ogloszenie saved) {
    return new AnnouncementResponse(saved.getId(), saved.getTresc(), saved.getWydarzenie().getId());
  }

  public record AddEmployeeRequest(String firstName, String lastName, Long parishId, Long positionId) {}

  public record EmployeeResponse(Long id, String firstName, String lastName, Long parishId, Long positionId) {}

  public record PositionResponse(Long id, String name, String description) {}

  public record AddDutyRequest(String name, String description, Long positionId) {}

  public record DutyResponse(Long id, String name, String description, Long positionId) {}

  public record AddIntentionRequest(String content, LocalDate date, String donor, Long eventId) {}

  public record IntentionResponse(Long id, String content, LocalDate date, String donor, Long eventId) {}

  public record AddAnnouncementRequest(String content, Long eventId) {}

  public record AnnouncementResponse(Long id, String content, Long eventId) {}
}
