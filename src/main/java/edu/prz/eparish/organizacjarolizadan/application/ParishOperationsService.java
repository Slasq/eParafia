package edu.prz.eparish.organizacjarolizadan.application;

import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.informacjeoparafii.domain.parafia.ParafiaRepozytorium;
import edu.prz.eparish.organizacjarolizadan.domain.obowiazek.Obowiazek;
import edu.prz.eparish.organizacjarolizadan.domain.obowiazek.ObowiazekRepozytorium;
import edu.prz.eparish.organizacjarolizadan.domain.pracownik.Pracownik;
import edu.prz.eparish.organizacjarolizadan.domain.pracownik.PracownikRepozytorium;
import edu.prz.eparish.organizacjarolizadan.domain.stanowisko.Stanowisko;
import edu.prz.eparish.organizacjarolizadan.domain.stanowisko.StanowiskoRepozytorium;
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
  private final PracownikRepozytorium employeeRepo;
  private final StanowiskoRepozytorium positionRepo;
  private final ObowiazekRepozytorium dutyRepo;
  private final ParafiaRepozytorium parishRepo;

  // ── Commands ────────────────────────────────────────────────────────────────

  public record AddEmployeeCommand(String firstName, String lastName, Long parishId, Long positionId) {}

  public record AddPositionCommand(String name, String description) {}

  public record AssignDutyCommand(String name, String description, Long positionId) {}

  // ── UC: Zarządzanie personelem parafii ──────────────────────────────────────

  public Pracownik addEmployee(AddEmployeeCommand cmd) {
    Parafia parish = parishRepo.findById(cmd.parishId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found"));
    Stanowisko position = requirePosition(cmd.positionId());
    Pracownik employee = factory.createEmployee(cmd.firstName(), cmd.lastName(), parish, position);
    return employeeRepo.save(employee);
  }

  public List<Pracownik> listEmployees() {
    return employeeRepo.findAll();
  }

  public Pracownik getEmployee(Long id) {
    return employeeRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
  }

  // ── UC: Przydzielanie stanowiska ─────────────────────────────────────────────

  public Stanowisko addPosition(AddPositionCommand cmd) {
    Stanowisko position = factory.createPosition(cmd.name(), cmd.description());
    return positionRepo.save(position);
  }

  public List<Stanowisko> listPositions() {
    return positionRepo.findAll();
  }

  // ── UC: Przydzielanie obowiązku ──────────────────────────────────────────────

  public Obowiazek assignDuty(AssignDutyCommand cmd) {
    Stanowisko position = requirePosition(cmd.positionId());
    Obowiazek duty = factory.createDuty(cmd.name(), cmd.description(), position);
    return dutyRepo.save(duty);
  }

  // ── UC: Wykonanie obowiązku (Pracownik realizuje zadanie) ────────────────────

  public Obowiazek completeDuty(Long dutyId) {
    Obowiazek duty = dutyRepo.findById(dutyId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Duty not found"));
    duty.setStatus("COMPLETED");
    return dutyRepo.save(duty);
  }

  public List<Obowiazek> listDuties() {
    return dutyRepo.findAll();
  }

  // ── Internal helpers ─────────────────────────────────────────────────────────

  private Stanowisko requirePosition(Long id) {
    return positionRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Position not found"));
  }
}
