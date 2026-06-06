package edu.prz.eparish.api;

import edu.prz.eparish.poslugasakramentalna.application.SacramentalMinistryService;
import edu.prz.eparish.poslugasakramentalna.application.SacramentalMinistryService.AddPriestCommand;
import edu.prz.eparish.poslugasakramentalna.application.SacramentalMinistryService.AddSacramentCommand;
import edu.prz.eparish.poslugasakramentalna.application.SacramentalMinistryService.RegisterSacramentCommand;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.Ksiadz;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.Sakrament;
import edu.prz.eparish.poslugasakramentalna.domain.udzielaniesakramentu.UdzielanieSakramentu;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Sacramental Ministry", description = "Priests, sacraments, administrations — UC: rejestrowanie sakramentów")
public class SacramentalMinistryController {

  private final SacramentalMinistryService service;

  // ── PRIESTS ──────────────────────────────────────────────────────────────────

  @GetMapping("/priests")
  @Operation(summary = "List all priests")
  public List<PriestResponse> listPriests() {
    return service.listPriests().stream().map(this::toPriestResponse).toList();
  }

  @GetMapping("/priests/{id}")
  @Operation(summary = "Get priest by ID")
  public PriestResponse getPriest(@PathVariable Long id) {
    return toPriestResponse(service.getPriest(id));
  }

  @PostMapping("/priests")
  @Operation(summary = "UC: Zarządzanie personelem — add priest")
  public ResponseEntity<PriestResponse> addPriest(@RequestBody AddPriestRequest req) {
    Ksiadz priest = service.addPriest(new AddPriestCommand(
        req.firstName(), req.lastName(), req.phone(), req.email(),
        req.ordinationDate(), req.role(), req.parishId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toPriestResponse(priest));
  }

  // ── SACRAMENTS ───────────────────────────────────────────────────────────────

  @GetMapping("/sacraments")
  @Operation(summary = "List all sacraments")
  public List<SacramentResponse> listSacraments() {
    return service.listSacraments().stream()
        .map(s -> new SacramentResponse(s.getId(), s.getNazwa(), s.getOpis()))
        .toList();
  }

  @PostMapping("/sacraments")
  @Operation(summary = "Create sacrament type")
  public ResponseEntity<SacramentResponse> addSacrament(@RequestBody AddSacramentRequest req) {
    Sakrament sacrament = service.addSacrament(new AddSacramentCommand(req.name(), req.description()));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new SacramentResponse(sacrament.getId(), sacrament.getNazwa(), sacrament.getOpis()));
  }

  // ── SACRAMENT ADMINISTRATIONS — UC: Rejestrowanie sakramentów ───────────────

  @GetMapping("/sacrament-administrations")
  @Operation(summary = "List all sacrament administrations")
  public List<AdministrationResponse> listAdministrations() {
    return service.listAdministrations().stream().map(this::toAdministrationResponse).toList();
  }

  @PostMapping("/sacrament-administrations")
  @Operation(summary = "UC: Rejestrowanie sakramentów — register sacrament administration")
  public ResponseEntity<AdministrationResponse> registerSacrament(@RequestBody AddAdministrationRequest req) {
    UdzielanieSakramentu admin = service.registerSacrament(new RegisterSacramentCommand(
        req.administrationDate(), req.parishionerId(), req.priestId(), req.sacramentId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toAdministrationResponse(admin));
  }

  // ── Mapping helpers ──────────────────────────────────────────────────────────

  private PriestResponse toPriestResponse(Ksiadz k) {
    Long parishId = k.getParafia() != null ? k.getParafia().getId() : null;
    return new PriestResponse(k.getId(), k.getImie(), k.getNazwisko(), k.getTelefon(),
        k.getEmail(), k.getDataSwiecen(), k.getFunkcja(), parishId);
  }

  private AdministrationResponse toAdministrationResponse(UdzielanieSakramentu u) {
    return new AdministrationResponse(u.getId(), u.getDataUdzielenia(),
        u.getParafianinId().wartosc(), u.getKsiadzId().wartosc(), u.getSakramentId().wartosc());
  }

  // ── Request / Response records ───────────────────────────────────────────────

  public record AddPriestRequest(String firstName, String lastName, String phone, String email,
      LocalDate ordinationDate, String role, Long parishId) {}
  public record PriestResponse(Long id, String firstName, String lastName, String phone, String email,
      LocalDate ordinationDate, String role, Long parishId) {}

  public record AddSacramentRequest(String name, String description) {}
  public record SacramentResponse(Long id, String name, String description) {}

  public record AddAdministrationRequest(LocalDate administrationDate,
      Long parishionerId, Long priestId, Long sacramentId) {}
  public record AdministrationResponse(Long id, LocalDate administrationDate,
      Long parishionerId, Long priestId, Long sacramentId) {}
}
