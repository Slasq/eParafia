package edu.prz.eparish.sacramentalministry.application;

import edu.prz.eparish.pastoralcare.domain.parishioner.ParishionerId;
import edu.prz.eparish.sacramentalministry.domain.priest.PriestId;
import edu.prz.eparish.pastoralcare.domain.parishioner.ParishionerRepository;
import edu.prz.eparish.parishinformation.domain.parish.Parish;
import edu.prz.eparish.parishinformation.domain.parish.ParishRepository;
import edu.prz.eparish.sacramentalministry.domain.priest.Priest;
import edu.prz.eparish.sacramentalministry.domain.priest.PriestRepository;
import edu.prz.eparish.sacramentalministry.domain.sacrament.Sacrament;
import edu.prz.eparish.sacramentalministry.domain.sacrament.SacramentId;
import edu.prz.eparish.sacramentalministry.domain.sacrament.SacramentRepository;
import edu.prz.eparish.sacramentalministry.domain.sacramentadministration.SacramentAdministration;
import edu.prz.eparish.sacramentalministry.domain.sacramentadministration.SacramentAdministrationRepository;
import edu.prz.eparish.api.support.ListFilterSupport;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class SacramentalMinistryService {

  private final SacramentalMinistryFactory factory;
  private final PriestRepository priestRepo;
  private final SacramentRepository sacramentRepo;
  private final SacramentAdministrationRepository administrationRepo;
  private final ParishRepository parishRepo;
  private final ParishionerRepository parishionerRepo;

  // ── Commands ────────────────────────────────────────────────────────────────

  public record AddPriestCommand(
      String firstName, String lastName, String phone, String email,
      LocalDate ordinationDate, String role, Long parishId) {}

  public record AddSacramentCommand(String name, String description) {}

  public record RegisterSacramentCommand(
      LocalDate administrationDate, Long parishionerId, Long priestId, Long sacramentId) {}

  public record PatchPriestCommand(
      String firstName, String lastName, String phone, String email,
      LocalDate ordinationDate, String role, Long parishId) {}

  public record PatchSacramentCommand(String name, String description) {}

  public record PatchAdministrationCommand(
      LocalDate administrationDate, Long parishionerId, Long priestId, Long sacramentId) {}

  // ── Zarządzanie księżmi ──────────────────────────────────────────────────────

  public Priest addPriest(AddPriestCommand cmd) {
    Parish parish = parishRepo.findById(cmd.parishId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found"));
    Priest priest = factory.createPriest(
        cmd.firstName(), cmd.lastName(), cmd.phone(), cmd.email(),
        cmd.ordinationDate(), cmd.role(), parish);
    return priestRepo.save(priest);
  }

  public Priest patchPriest(Long id, PatchPriestCommand cmd) {
    Priest priest = getPriest(id);
    if (cmd.firstName() != null) {
      priest.setFirstName(cmd.firstName());
    }
    if (cmd.lastName() != null) {
      priest.setLastName(cmd.lastName());
    }
    if (cmd.phone() != null) {
      priest.setPhone(cmd.phone());
    }
    if (cmd.email() != null) {
      priest.setEmail(cmd.email());
    }
    if (cmd.ordinationDate() != null) {
      priest.setOrdinationDate(cmd.ordinationDate());
    }
    if (cmd.role() != null) {
      priest.setRole(cmd.role());
    }
    if (cmd.parishId() != null) {
      Parish parish = parishRepo.findById(cmd.parishId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found"));
      priest.setParish(parish);
    }
    return priestRepo.save(priest);
  }

  public void deletePriest(Long id) {
    if (!priestRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Priest not found");
    }
    priestRepo.deleteById(id);
  }

  public List<Priest> listPriests(Long parishId, String role) {
    return ListFilterSupport.filter(priestRepo.findAll(),
        ListFilterSupport.eqLong(parishId, k -> k.getParish() != null ? k.getParish().getId() : null),
        ListFilterSupport.eq(role, Priest::getRole));
  }

  public Priest getPriest(Long id) {
    return priestRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Priest not found"));
  }

  // ── Zarządzanie sakramentami ──────────────────────────────────────────────────

  public Sacrament addSacrament(AddSacramentCommand cmd) {
    Sacrament sacrament = factory.createSacrament(cmd.name(), cmd.description());
    return sacramentRepo.save(sacrament);
  }

  public Sacrament patchSacrament(Long id, PatchSacramentCommand cmd) {
    Sacrament sacrament = sacramentRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sacrament not found"));
    if (cmd.name() != null) {
      sacrament.setName(cmd.name());
    }
    if (cmd.description() != null) {
      sacrament.setDescription(cmd.description());
    }
    return sacramentRepo.save(sacrament);
  }

  public void deleteSacrament(Long id) {
    if (!sacramentRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sacrament not found");
    }
    sacramentRepo.deleteById(id);
  }

  public List<Sacrament> listSacraments(String name) {
    return ListFilterSupport.filter(sacramentRepo.findAll(),
        ListFilterSupport.containsIgnoreCase(name, Sacrament::getName));
  }

  public Sacrament getSacrament(Long id) {
    return sacramentRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sacrament not found"));
  }

  // ── UC: Rejestrowanie sakramentów ────────────────────────────────────────────

  public SacramentAdministration registerSacrament(RegisterSacramentCommand cmd) {
    if (!parishionerRepo.existsById(cmd.parishionerId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner not found");
    }
    if (!priestRepo.existsById(cmd.priestId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Priest not found");
    }
    if (!sacramentRepo.existsById(cmd.sacramentId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sacrament not found");
    }
    SacramentAdministration administration = factory.createAdministration(
        cmd.administrationDate(), cmd.parishionerId(), cmd.priestId(), cmd.sacramentId());
    return administrationRepo.save(administration);
  }

  public SacramentAdministration getAdministration(Long id) {
    return administrationRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Administration not found"));
  }

  public SacramentAdministration patchAdministration(Long id, PatchAdministrationCommand cmd) {
    SacramentAdministration administration = getAdministration(id);
    if (cmd.administrationDate() != null) {
      administration.setAdministrationDate(cmd.administrationDate());
    }
    if (cmd.parishionerId() != null) {
      if (!parishionerRepo.existsById(cmd.parishionerId())) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner not found");
      }
      administration.setParishionerId(new ParishionerId(cmd.parishionerId()));
    }
    if (cmd.priestId() != null) {
      if (!priestRepo.existsById(cmd.priestId())) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Priest not found");
      }
      administration.setPriestId(new PriestId(cmd.priestId()));
    }
    if (cmd.sacramentId() != null) {
      if (!sacramentRepo.existsById(cmd.sacramentId())) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sacrament not found");
      }
      administration.setSacramentId(new SacramentId(cmd.sacramentId()));
    }
    return administrationRepo.save(administration);
  }

  public void deleteAdministration(Long id) {
    if (!administrationRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Administration not found");
    }
    administrationRepo.deleteById(id);
  }

  public List<SacramentAdministration> listAdministrations(
      Long parishionerId, Long priestId, Long sacramentId, LocalDate administrationDate) {
    return ListFilterSupport.filter(administrationRepo.findAll(),
        ListFilterSupport.eqLong(parishionerId, a -> a.getParishionerId().value()),
        ListFilterSupport.eqLong(priestId, a -> a.getPriestId().value()),
        ListFilterSupport.eqLong(sacramentId, a -> a.getSacramentId().value()),
        ListFilterSupport.eq(administrationDate, SacramentAdministration::getAdministrationDate));
  }
}
