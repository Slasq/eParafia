package edu.prz.eparish.api;

import edu.prz.eparish.organizacjarolizadan.application.ParishOperationsService;
import edu.prz.eparish.organizacjarolizadan.application.ParishOperationsService.AddEmployeeCommand;
import edu.prz.eparish.organizacjarolizadan.application.ParishOperationsService.AddPositionCommand;
import edu.prz.eparish.organizacjarolizadan.application.ParishOperationsService.AssignDutyCommand;
import edu.prz.eparish.organizacjarolizadan.domain.obowiazek.Obowiazek;
import edu.prz.eparish.organizacjarolizadan.domain.pracownik.Pracownik;
import edu.prz.eparish.organizacjarolizadan.domain.stanowisko.Stanowisko;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Parish Operations", description = "Staff, positions, duties — UC: zarządzanie personelem")
public class ParishOperationsController {

  private final ParishOperationsService service;

  // ── EMPLOYEES — UC: Zarządzanie personelem parafii ──────────────────────────

  @GetMapping("/employees")
  @Operation(summary = "List all employees")
  public List<EmployeeResponse> listEmployees() {
    return service.listEmployees().stream().map(this::toEmployeeResponse).toList();
  }

  @GetMapping("/employees/{id}")
  @Operation(summary = "Get employee by ID")
  public EmployeeResponse getEmployee(@PathVariable Long id) {
    return toEmployeeResponse(service.getEmployee(id));
  }

  @PostMapping("/employees")
  @Operation(summary = "UC: Zarządzanie personelem — add employee")
  public ResponseEntity<EmployeeResponse> addEmployee(@RequestBody AddEmployeeRequest req) {
    Pracownik employee = service.addEmployee(
        new AddEmployeeCommand(req.firstName(), req.lastName(), req.parishId(), req.positionId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toEmployeeResponse(employee));
  }

  // ── POSITIONS — UC: Przydzielanie stanowiska ─────────────────────────────────

  @GetMapping("/positions")
  @Operation(summary = "List all positions")
  public List<PositionResponse> listPositions() {
    return service.listPositions().stream().map(this::toPositionResponse).toList();
  }

  @PostMapping("/positions")
  @Operation(summary = "UC: Przydzielanie stanowiska — create position")
  public ResponseEntity<PositionResponse> addPosition(@RequestBody AddPositionRequest req) {
    Stanowisko position = service.addPosition(new AddPositionCommand(req.name(), req.description()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toPositionResponse(position));
  }

  // ── DUTIES — UC: Przydzielanie obowiązku / Wykonanie obowiązku ───────────────

  @GetMapping("/duties")
  @Operation(summary = "List all duties")
  public List<DutyResponse> listDuties() {
    return service.listDuties().stream().map(this::toDutyResponse).toList();
  }

  @PostMapping("/duties")
  @Operation(summary = "UC: Przydzielanie obowiązku — assign duty to position")
  public ResponseEntity<DutyResponse> addDuty(@RequestBody AddDutyRequest req) {
    Obowiazek duty = service.assignDuty(
        new AssignDutyCommand(req.name(), req.description(), req.positionId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toDutyResponse(duty));
  }

  @PutMapping("/duties/{id}/complete")
  @Operation(summary = "UC: Wykonanie obowiązku — employee completes duty")
  public DutyResponse completeDuty(@PathVariable Long id) {
    return toDutyResponse(service.completeDuty(id));
  }

  // ── Mapping helpers ──────────────────────────────────────────────────────────

  private EmployeeResponse toEmployeeResponse(Pracownik p) {
    return new EmployeeResponse(p.getId(), p.getImie(), p.getNazwisko(),
        p.getParafia().getId(), p.getStanowisko().getId());
  }

  private PositionResponse toPositionResponse(Stanowisko s) {
    return new PositionResponse(s.getId(), s.getNazwa(), s.getOpis());
  }

  private DutyResponse toDutyResponse(Obowiazek o) {
    return new DutyResponse(o.getId(), o.getNazwa(), o.getOpis(),
        o.getStatus(), o.getStanowisko().getId());
  }

  // ── Request / Response records ───────────────────────────────────────────────

  public record AddEmployeeRequest(String firstName, String lastName, Long parishId, Long positionId) {}
  public record EmployeeResponse(Long id, String firstName, String lastName, Long parishId, Long positionId) {}

  public record AddPositionRequest(String name, String description) {}
  public record PositionResponse(Long id, String name, String description) {}

  public record AddDutyRequest(String name, String description, Long positionId) {}
  public record DutyResponse(Long id, String name, String description, String status, Long positionId) {}
}
