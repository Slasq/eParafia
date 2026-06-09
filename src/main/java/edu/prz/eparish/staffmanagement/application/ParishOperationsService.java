package edu.prz.eparish.staffmanagement.application;

import edu.prz.eparish.parishinformation.domain.parish.Parish;
import edu.prz.eparish.parishinformation.domain.parish.ParishRepository;
import edu.prz.eparish.staffmanagement.domain.duty.Duty;
import edu.prz.eparish.staffmanagement.domain.duty.DutyRepository;
import edu.prz.eparish.staffmanagement.domain.employee.Employee;
import edu.prz.eparish.staffmanagement.domain.employee.EmployeeRepository;
import edu.prz.eparish.staffmanagement.domain.position.Position;
import edu.prz.eparish.staffmanagement.domain.position.PositionRepository;
import edu.prz.eparish.api.support.ListFilterSupport;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class ParishOperationsService {

  private final StaffFactory factory;
  private final EmployeeRepository employeeRepo;
  private final PositionRepository positionRepo;
  private final DutyRepository dutyRepo;
  private final ParishRepository parishRepo;

  // ── Commands ────────────────────────────────────────────────────────────────

  public record AddEmployeeCommand(String firstName, String lastName, Long parishId, Long positionId) {}

  public record AddPositionCommand(String name, String description) {}

  public record AssignDutyCommand(String name, String description, Long positionId) {}

  public record PatchEmployeeCommand(String firstName, String lastName, Long parishId, Long positionId) {}

  public record PatchPositionCommand(String name, String description) {}

  public record PatchDutyCommand(String name, String description, String status) {}

  // ── UC: Zarządzanie personelem parafii ──────────────────────────────────────

  public Employee addEmployee(AddEmployeeCommand cmd) {
    Parish parish = parishRepo.findById(cmd.parishId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found"));
    Position position = requirePosition(cmd.positionId());
    Employee employee = factory.createEmployee(cmd.firstName(), cmd.lastName(), parish, position);
    return employeeRepo.save(employee);
  }

  public Employee patchEmployee(Long id, PatchEmployeeCommand cmd) {
    Employee employee = getEmployee(id);
    if (cmd.firstName() != null) {
      employee.setFirstName(cmd.firstName());
    }
    if (cmd.lastName() != null) {
      employee.setLastName(cmd.lastName());
    }
    if (cmd.parishId() != null) {
      Parish parish = parishRepo.findById(cmd.parishId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found"));
      employee.setParish(parish);
    }
    if (cmd.positionId() != null) {
      employee.setPosition(requirePosition(cmd.positionId()));
    }
    return employeeRepo.save(employee);
  }

  public void deleteEmployee(Long id) {
    if (!employeeRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
    }
    employeeRepo.deleteById(id);
  }

  public List<Employee> listEmployees(Long parishId, Long positionId) {
    return ListFilterSupport.filter(employeeRepo.findAll(),
        ListFilterSupport.eqLong(parishId, e -> e.getParish() != null ? e.getParish().getId() : null),
        ListFilterSupport.eqLong(positionId, e -> e.getPosition() != null ? e.getPosition().getId() : null));
  }

  public Employee getEmployee(Long id) {
    return employeeRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
  }

  // ── UC: Przydzielanie stanowiska ─────────────────────────────────────────────

  public Position addPosition(AddPositionCommand cmd) {
    Position position = factory.createPosition(cmd.name(), cmd.description());
    return positionRepo.save(position);
  }

  public Position patchPosition(Long id, PatchPositionCommand cmd) {
    Position position = requirePosition(id);
    if (cmd.name() != null) {
      position.setName(cmd.name());
    }
    if (cmd.description() != null) {
      position.setDescription(cmd.description());
    }
    return positionRepo.save(position);
  }

  public void deletePosition(Long id) {
    if (!positionRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Position not found");
    }
    positionRepo.deleteById(id);
  }

  public List<Position> listPositions(String name) {
    return ListFilterSupport.filter(positionRepo.findAll(),
        ListFilterSupport.containsIgnoreCase(name, Position::getName));
  }

  public Position getPosition(Long id) {
    return requirePosition(id);
  }

  // ── UC: Przydzielanie obowiązku ──────────────────────────────────────────────

  public Duty assignDuty(AssignDutyCommand cmd) {
    Position position = requirePosition(cmd.positionId());
    Duty duty = factory.createDuty(cmd.name(), cmd.description(), position);
    return dutyRepo.save(duty);
  }

  // ── UC: Wykonanie obowiązku (Pracownik realizuje zadanie) ────────────────────

  public Duty completeDuty(Long dutyId) {
    Duty duty = dutyRepo.findById(dutyId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Duty not found"));
    duty.setStatus("COMPLETED");
    return dutyRepo.save(duty);
  }

  public Duty patchDuty(Long dutyId, PatchDutyCommand cmd) {
    Duty duty = dutyRepo.findById(dutyId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Duty not found"));
    if (cmd.name() != null) {
      duty.setName(cmd.name());
    }
    if (cmd.description() != null) {
      duty.setDescription(cmd.description());
    }
    if (cmd.status() != null) {
      duty.setStatus(cmd.status());
    }
    return dutyRepo.save(duty);
  }

  public void deleteDuty(Long dutyId) {
    if (!dutyRepo.existsById(dutyId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Duty not found");
    }
    dutyRepo.deleteById(dutyId);
  }

  public List<Duty> listDuties(Long positionId, String status) {
    return ListFilterSupport.filter(dutyRepo.findAll(),
        ListFilterSupport.eqLong(positionId, d -> d.getPosition() != null ? d.getPosition().getId() : null),
        ListFilterSupport.eq(status, Duty::getStatus));
  }

  // ── Internal helpers ─────────────────────────────────────────────────────────

  private Position requirePosition(Long id) {
    return positionRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Position not found"));
  }
}
