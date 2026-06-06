package edu.prz.eparish.organizacjarolizadan.application;

import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.informacjeoparafii.domain.parafia.ParafiaRepozytorium;
import edu.prz.eparish.organizacjarolizadan.domain.obowiazek.Obowiazek;
import edu.prz.eparish.organizacjarolizadan.domain.obowiazek.ObowiazekRepozytorium;
import edu.prz.eparish.organizacjarolizadan.domain.pracownik.Pracownik;
import edu.prz.eparish.organizacjarolizadan.domain.pracownik.PracownikRepozytorium;
import edu.prz.eparish.organizacjarolizadan.domain.stanowisko.Stanowisko;
import edu.prz.eparish.organizacjarolizadan.domain.stanowisko.StanowiskoRepozytorium;
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
  private final PracownikRepozytorium employeeRepo;
  private final StanowiskoRepozytorium positionRepo;
  private final ObowiazekRepozytorium dutyRepo;
  private final ParafiaRepozytorium parishRepo;

  // ── Commands ────────────────────────────────────────────────────────────────

  public record AddEmployeeCommand(String firstName, String lastName, Long parishId, Long positionId) {}

  public record AddPositionCommand(String name, String description) {}

  public record AssignDutyCommand(String name, String description, Long positionId) {}

  public record PatchEmployeeCommand(String firstName, String lastName, Long parishId, Long positionId) {}

  public record PatchPositionCommand(String name, String description) {}

  public record PatchDutyCommand(String name, String description, String status) {}

  // ── UC: Zarządzanie personelem parafii ──────────────────────────────────────

  public Pracownik addEmployee(AddEmployeeCommand cmd) {
    Parafia parish = parishRepo.findById(cmd.parishId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found"));
    Stanowisko position = requirePosition(cmd.positionId());
    Pracownik employee = factory.createEmployee(cmd.firstName(), cmd.lastName(), parish, position);
    return employeeRepo.save(employee);
  }

  public Pracownik patchEmployee(Long id, PatchEmployeeCommand cmd) {
    Pracownik employee = getEmployee(id);
    if (cmd.firstName() != null) {
      employee.setImie(cmd.firstName());
    }
    if (cmd.lastName() != null) {
      employee.setNazwisko(cmd.lastName());
    }
    if (cmd.parishId() != null) {
      Parafia parish = parishRepo.findById(cmd.parishId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found"));
      employee.setParafia(parish);
    }
    if (cmd.positionId() != null) {
      employee.setStanowisko(requirePosition(cmd.positionId()));
    }
    return employeeRepo.save(employee);
  }

  public void deleteEmployee(Long id) {
    if (!employeeRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
    }
    employeeRepo.deleteById(id);
  }

  public List<Pracownik> listEmployees(Long parishId, Long positionId) {
    return ListFilterSupport.filter(employeeRepo.findAll(),
        ListFilterSupport.eqLong(parishId, e -> e.getParafia() != null ? e.getParafia().getId() : null),
        ListFilterSupport.eqLong(positionId, e -> e.getStanowisko() != null ? e.getStanowisko().getId() : null));
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

  public Stanowisko patchPosition(Long id, PatchPositionCommand cmd) {
    Stanowisko position = requirePosition(id);
    if (cmd.name() != null) {
      position.setNazwa(cmd.name());
    }
    if (cmd.description() != null) {
      position.setOpis(cmd.description());
    }
    return positionRepo.save(position);
  }

  public void deletePosition(Long id) {
    if (!positionRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Position not found");
    }
    positionRepo.deleteById(id);
  }

  public List<Stanowisko> listPositions(String name) {
    return ListFilterSupport.filter(positionRepo.findAll(),
        ListFilterSupport.containsIgnoreCase(name, Stanowisko::getNazwa));
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

  public Obowiazek patchDuty(Long dutyId, PatchDutyCommand cmd) {
    Obowiazek duty = dutyRepo.findById(dutyId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Duty not found"));
    if (cmd.name() != null) {
      duty.setNazwa(cmd.name());
    }
    if (cmd.description() != null) {
      duty.setOpis(cmd.description());
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

  public List<Obowiazek> listDuties(Long positionId, String status) {
    return ListFilterSupport.filter(dutyRepo.findAll(),
        ListFilterSupport.eqLong(positionId, d -> d.getStanowisko() != null ? d.getStanowisko().getId() : null),
        ListFilterSupport.eq(status, Obowiazek::getStatus));
  }

  // ── Internal helpers ─────────────────────────────────────────────────────────

  private Stanowisko requirePosition(Long id) {
    return positionRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Position not found"));
  }
}
