package edu.prz.eparish.api;

import edu.prz.eparish.staffmanagement.application.ParishOperationsService;
import edu.prz.eparish.staffmanagement.application.ParishOperationsService.AddEmployeeCommand;
import edu.prz.eparish.staffmanagement.application.ParishOperationsService.AddPositionCommand;
import edu.prz.eparish.staffmanagement.application.ParishOperationsService.AssignDutyCommand;
import edu.prz.eparish.staffmanagement.domain.duty.Duty;
import edu.prz.eparish.staffmanagement.domain.employee.Employee;
import edu.prz.eparish.staffmanagement.domain.position.Position;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Parish Operations", description = "Staff, positions, duties — UC: zarządzanie personelem")
public class ParishOperationsController {

  private final ParishOperationsService service;

  // ── EMPLOYEES — UC: Zarządzanie personelem parafii ──────────────────────────

  @GetMapping("/employees")
  @Operation(summary = "List employees (optional filters: parishId, positionId)")
  public List<EmployeeResponse> listEmployees(
      @RequestParam(required = false) Long parishId,
      @RequestParam(required = false) Long positionId) {
    return service.listEmployees(parishId, positionId).stream().map(this::toEmployeeResponse).toList();
  }

  @GetMapping("/employees/{id}")
  @Operation(summary = "Get employee by ID")
  public EmployeeResponse getEmployee(@PathVariable Long id) {
    return toEmployeeResponse(service.getEmployee(id));
  }

  @PostMapping("/employees")
  @Operation(summary = "UC: Zarządzanie personelem — add employee")
  public ResponseEntity<EmployeeResponse> addEmployee(@RequestBody AddEmployeeRequest req) {
    Employee employee = service.addEmployee(
        new AddEmployeeCommand(req.firstName(), req.lastName(), req.parishId(), req.positionId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toEmployeeResponse(employee));
  }

  @PatchMapping("/employees/{id}")
  @Operation(summary = "Partially update employee")
  public EmployeeResponse patchEmployee(@PathVariable Long id, @RequestBody PatchEmployeeRequest req) {
    return toEmployeeResponse(service.patchEmployee(id,
        new ParishOperationsService.PatchEmployeeCommand(
            req.firstName(), req.lastName(), req.parishId(), req.positionId())));
  }

  @DeleteMapping("/employees/{id}")
  @Operation(summary = "Delete employee")
  public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
    service.deleteEmployee(id);
    return ResponseEntity.noContent().build();
  }

  // ── POSITIONS — UC: Przydzielanie stanowiska ─────────────────────────────────

  @GetMapping("/positions")
  @Operation(summary = "List positions (optional filter: name)")
  public List<PositionResponse> listPositions(@RequestParam(required = false) String name) {
    return service.listPositions(name).stream().map(this::toPositionResponse).toList();
  }

  @GetMapping("/positions/{id}")
  @Operation(summary = "Get position by ID")
  public PositionResponse getPosition(@PathVariable Long id) {
    return toPositionResponse(service.getPosition(id));
  }

  @PostMapping("/positions")
  @Operation(summary = "UC: Przydzielanie stanowiska — create position")
  public ResponseEntity<PositionResponse> addPosition(@RequestBody AddPositionRequest req) {
    Position position = service.addPosition(new AddPositionCommand(req.name(), req.description()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toPositionResponse(position));
  }

  @PatchMapping("/positions/{id}")
  @Operation(summary = "Partially update position")
  public PositionResponse patchPosition(@PathVariable Long id, @RequestBody PatchPositionRequest req) {
    return toPositionResponse(service.patchPosition(id,
        new ParishOperationsService.PatchPositionCommand(req.name(), req.description())));
  }

  @DeleteMapping("/positions/{id}")
  @Operation(summary = "Delete position")
  public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
    service.deletePosition(id);
    return ResponseEntity.noContent().build();
  }

  // ── DUTIES — UC: Przydzielanie obowiązku / Wykonanie obowiązku ───────────────

  @GetMapping("/duties")
  @Operation(summary = "List duties (optional filters: positionId, status)")
  public List<DutyResponse> listDuties(
      @RequestParam(required = false) Long positionId,
      @RequestParam(required = false) String status) {
    return service.listDuties(positionId, status).stream().map(this::toDutyResponse).toList();
  }

  @PostMapping("/duties")
  @Operation(summary = "UC: Przydzielanie obowiązku — assign duty to position")
  public ResponseEntity<DutyResponse> addDuty(@RequestBody AddDutyRequest req) {
    Duty duty = service.assignDuty(
        new AssignDutyCommand(req.name(), req.description(), req.positionId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toDutyResponse(duty));
  }

  @PatchMapping("/duties/{id}")
  @Operation(summary = "Partially update duty")
  public DutyResponse patchDuty(@PathVariable Long id, @RequestBody PatchDutyRequest req) {
    return toDutyResponse(service.patchDuty(id,
        new ParishOperationsService.PatchDutyCommand(req.name(), req.description(), req.status())));
  }

  @DeleteMapping("/duties/{id}")
  @Operation(summary = "Delete duty")
  public ResponseEntity<Void> deleteDuty(@PathVariable Long id) {
    service.deleteDuty(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/duties/{id}/complete")
  @Operation(summary = "UC: Wykonanie obowiązku — employee completes duty")
  public DutyResponse completeDuty(@PathVariable Long id) {
    return toDutyResponse(service.completeDuty(id));
  }

  // ── Mapping helpers ──────────────────────────────────────────────────────────

  private EmployeeResponse toEmployeeResponse(Employee p) {
    return new EmployeeResponse(p.getId(), p.getFirstName(), p.getLastName(),
        p.getParish().getId(), p.getPosition().getId());
  }

  private PositionResponse toPositionResponse(Position s) {
    return new PositionResponse(s.getId(), s.getName(), s.getDescription());
  }

  private DutyResponse toDutyResponse(Duty o) {
    return new DutyResponse(o.getId(), o.getName(), o.getDescription(),
        o.getStatus(), o.getPosition().getId());
  }

  // ── Request / Response records ───────────────────────────────────────────────

  public record AddEmployeeRequest(String firstName, String lastName, Long parishId, Long positionId) {}
  public record PatchEmployeeRequest(String firstName, String lastName, Long parishId, Long positionId) {}
  public record EmployeeResponse(Long id, String firstName, String lastName, Long parishId, Long positionId) {}

  public record AddPositionRequest(String name, String description) {}
  public record PatchPositionRequest(String name, String description) {}
  public record PositionResponse(Long id, String name, String description) {}

  public record AddDutyRequest(String name, String description, Long positionId) {}
  public record PatchDutyRequest(String name, String description, String status) {}
  public record DutyResponse(Long id, String name, String description, String status, Long positionId) {}
}
