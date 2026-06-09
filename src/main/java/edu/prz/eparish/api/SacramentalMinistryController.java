package edu.prz.eparish.api;

import edu.prz.eparish.sacramentalministry.application.SacramentalMinistryService;
import edu.prz.eparish.sacramentalministry.application.SacramentalMinistryService.AddPriestCommand;
import edu.prz.eparish.sacramentalministry.application.SacramentalMinistryService.AddSacramentCommand;
import edu.prz.eparish.sacramentalministry.application.SacramentalMinistryService.RegisterSacramentCommand;
import edu.prz.eparish.sacramentalministry.domain.priest.Priest;
import edu.prz.eparish.sacramentalministry.domain.sacrament.Sacrament;
import edu.prz.eparish.sacramentalministry.domain.sacramentadministration.SacramentAdministration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Sacramental Ministry", description = "Priests, sacraments, administrations — UC: rejestrowanie sakramentów")
public class SacramentalMinistryController {

  private final SacramentalMinistryService service;

  // ── PRIESTS ──────────────────────────────────────────────────────────────────

  @GetMapping("/priests")
  @Operation(summary = "List priests (optional filters: parishId, role)")
  public List<PriestResponse> listPriests(
      @RequestParam(required = false) Long parishId,
      @RequestParam(required = false) String role) {
    return service.listPriests(parishId, role).stream().map(this::toPriestResponse).toList();
  }

  @GetMapping("/priests/{id}")
  @Operation(summary = "Get priest by ID")
  public PriestResponse getPriest(@PathVariable Long id) {
    return toPriestResponse(service.getPriest(id));
  }

  @PostMapping("/priests")
  @Operation(summary = "UC: Zarządzanie personelem — add priest")
  public ResponseEntity<PriestResponse> addPriest(@RequestBody AddPriestRequest req) {
    Priest priest = service.addPriest(new AddPriestCommand(
        req.firstName(), req.lastName(), req.phone(), req.email(),
        req.ordinationDate(), req.role(), req.parishId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toPriestResponse(priest));
  }

  @PatchMapping("/priests/{id}")
  @Operation(summary = "Partially update priest")
  public PriestResponse patchPriest(@PathVariable Long id, @RequestBody PatchPriestRequest req) {
    return toPriestResponse(service.patchPriest(id,
        new SacramentalMinistryService.PatchPriestCommand(
            req.firstName(), req.lastName(), req.phone(), req.email(),
            req.ordinationDate(), req.role(), req.parishId())));
  }

  @DeleteMapping("/priests/{id}")
  @Operation(summary = "Delete priest")
  public ResponseEntity<Void> deletePriest(@PathVariable Long id) {
    service.deletePriest(id);
    return ResponseEntity.noContent().build();
  }

  // ── SACRAMENTS ───────────────────────────────────────────────────────────────

  @GetMapping("/sacraments")
  @Operation(summary = "List sacraments (optional filter: name)")
  public List<SacramentResponse> listSacraments(@RequestParam(required = false) String name) {
    return service.listSacraments(name).stream()
        .map(s -> new SacramentResponse(s.getId(), s.getName(), s.getDescription()))
        .toList();
  }

  @GetMapping("/sacraments/{id}")
  @Operation(summary = "Get sacrament by ID")
  public SacramentResponse getSacrament(@PathVariable Long id) {
    Sacrament sacrament = service.getSacrament(id);
    return new SacramentResponse(sacrament.getId(), sacrament.getName(), sacrament.getDescription());
  }

  @PostMapping("/sacraments")
  @Operation(summary = "Create sacrament type")
  public ResponseEntity<SacramentResponse> addSacrament(@RequestBody AddSacramentRequest req) {
    Sacrament sacrament = service.addSacrament(new AddSacramentCommand(req.name(), req.description()));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new SacramentResponse(sacrament.getId(), sacrament.getName(), sacrament.getDescription()));
  }

  @PatchMapping("/sacraments/{id}")
  @Operation(summary = "Partially update sacrament")
  public SacramentResponse patchSacrament(@PathVariable Long id, @RequestBody PatchSacramentRequest req) {
    Sacrament sacrament = service.patchSacrament(id,
        new SacramentalMinistryService.PatchSacramentCommand(req.name(), req.description()));
    return new SacramentResponse(sacrament.getId(), sacrament.getName(), sacrament.getDescription());
  }

  @DeleteMapping("/sacraments/{id}")
  @Operation(summary = "Delete sacrament")
  public ResponseEntity<Void> deleteSacrament(@PathVariable Long id) {
    service.deleteSacrament(id);
    return ResponseEntity.noContent().build();
  }

  // ── SACRAMENT ADMINISTRATIONS — UC: Rejestrowanie sakramentów ───────────────

  @GetMapping("/sacrament-administrations")
  @Operation(summary = "List administrations (optional filters: parishionerId, priestId, sacramentId, administrationDate)")
  public List<AdministrationResponse> listAdministrations(
      @RequestParam(required = false) Long parishionerId,
      @RequestParam(required = false) Long priestId,
      @RequestParam(required = false) Long sacramentId,
      @RequestParam(required = false) LocalDate administrationDate) {
    return service.listAdministrations(parishionerId, priestId, sacramentId, administrationDate).stream()
        .map(this::toAdministrationResponse).toList();
  }

  @PostMapping("/sacrament-administrations")
  @Operation(summary = "UC: Rejestrowanie sakramentów — register sacrament administration")
  public ResponseEntity<AdministrationResponse> registerSacrament(@RequestBody AddAdministrationRequest req) {
    SacramentAdministration admin = service.registerSacrament(new RegisterSacramentCommand(
        req.administrationDate(), req.parishionerId(), req.priestId(), req.sacramentId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toAdministrationResponse(admin));
  }

  @GetMapping("/sacrament-administrations/{id}")
  @Operation(summary = "Get sacrament administration by ID")
  public AdministrationResponse getAdministration(@PathVariable Long id) {
    return toAdministrationResponse(service.getAdministration(id));
  }

  @PatchMapping("/sacrament-administrations/{id}")
  @Operation(summary = "Partially update sacrament administration")
  public AdministrationResponse patchAdministration(
      @PathVariable Long id, @RequestBody PatchAdministrationRequest req) {
    return toAdministrationResponse(service.patchAdministration(id,
        new SacramentalMinistryService.PatchAdministrationCommand(
            req.administrationDate(), req.parishionerId(), req.priestId(), req.sacramentId())));
  }

  @DeleteMapping("/sacrament-administrations/{id}")
  @Operation(summary = "Delete sacrament administration")
  public ResponseEntity<Void> deleteAdministration(@PathVariable Long id) {
    service.deleteAdministration(id);
    return ResponseEntity.noContent().build();
  }

  // ── Mapping helpers ──────────────────────────────────────────────────────────

  private PriestResponse toPriestResponse(Priest k) {
    Long parishId = k.getParish() != null ? k.getParish().getId() : null;
    return new PriestResponse(k.getId(), k.getFirstName(), k.getLastName(), k.getPhone(),
        k.getEmail(), k.getOrdinationDate(), k.getRole(), parishId);
  }

  private AdministrationResponse toAdministrationResponse(SacramentAdministration u) {
    return new AdministrationResponse(u.getId(), u.getAdministrationDate(),
        u.getParishionerId().value(), u.getPriestId().value(), u.getSacramentId().value());
  }

  // ── Request / Response records ───────────────────────────────────────────────

  public record AddPriestRequest(String firstName, String lastName, String phone, String email,
      LocalDate ordinationDate, String role, Long parishId) {}
  public record PatchPriestRequest(String firstName, String lastName, String phone, String email,
      LocalDate ordinationDate, String role, Long parishId) {}
  public record PriestResponse(Long id, String firstName, String lastName, String phone, String email,
      LocalDate ordinationDate, String role, Long parishId) {}

  public record AddSacramentRequest(String name, String description) {}
  public record PatchSacramentRequest(String name, String description) {}
  public record SacramentResponse(Long id, String name, String description) {}

  public record AddAdministrationRequest(LocalDate administrationDate,
      Long parishionerId, Long priestId, Long sacramentId) {}
  public record PatchAdministrationRequest(LocalDate administrationDate,
      Long parishionerId, Long priestId, Long sacramentId) {}
  public record AdministrationResponse(Long id, LocalDate administrationDate,
      Long parishionerId, Long priestId, Long sacramentId) {}
}
