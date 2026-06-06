package edu.prz.eparish.poslugasakramentalna.application;

import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinRepozytorium;
import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.informacjeoparafii.domain.parafia.ParafiaRepozytorium;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.Ksiadz;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.KsiadzRepozytorium;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.Sakrament;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.SakramentRepozytorium;
import edu.prz.eparish.poslugasakramentalna.domain.udzielaniesakramentu.UdzielanieSakramentu;
import edu.prz.eparish.poslugasakramentalna.domain.udzielaniesakramentu.UdzielanieSakramentuRepozytorium;
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
  private final KsiadzRepozytorium priestRepo;
  private final SakramentRepozytorium sacramentRepo;
  private final UdzielanieSakramentuRepozytorium administrationRepo;
  private final ParafiaRepozytorium parishRepo;
  private final ParafianinRepozytorium parishionerRepo;

  // ── Commands ────────────────────────────────────────────────────────────────

  public record AddPriestCommand(
      String firstName, String lastName, String phone, String email,
      LocalDate ordinationDate, String role, Long parishId) {}

  public record AddSacramentCommand(String name, String description) {}

  public record RegisterSacramentCommand(
      LocalDate administrationDate, Long parishionerId, Long priestId, Long sacramentId) {}

  // ── Zarządzanie księżmi ──────────────────────────────────────────────────────

  public Ksiadz addPriest(AddPriestCommand cmd) {
    Parafia parish = parishRepo.findById(cmd.parishId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found"));
    Ksiadz priest = factory.createPriest(
        cmd.firstName(), cmd.lastName(), cmd.phone(), cmd.email(),
        cmd.ordinationDate(), cmd.role(), parish);
    return priestRepo.save(priest);
  }

  public List<Ksiadz> listPriests() {
    return priestRepo.findAll();
  }

  public Ksiadz getPriest(Long id) {
    return priestRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Priest not found"));
  }

  // ── Zarządzanie sakramentami ──────────────────────────────────────────────────

  public Sakrament addSacrament(AddSacramentCommand cmd) {
    Sakrament sacrament = factory.createSacrament(cmd.name(), cmd.description());
    return sacramentRepo.save(sacrament);
  }

  public List<Sakrament> listSacraments() {
    return sacramentRepo.findAll();
  }

  // ── UC: Rejestrowanie sakramentów ────────────────────────────────────────────

  public UdzielanieSakramentu registerSacrament(RegisterSacramentCommand cmd) {
    if (!parishionerRepo.existsById(cmd.parishionerId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner not found");
    }
    if (!priestRepo.existsById(cmd.priestId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Priest not found");
    }
    if (!sacramentRepo.existsById(cmd.sacramentId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sacrament not found");
    }
    UdzielanieSakramentu administration = factory.createAdministration(
        cmd.administrationDate(), cmd.parishionerId(), cmd.priestId(), cmd.sacramentId());
    return administrationRepo.save(administration);
  }

  public List<UdzielanieSakramentu> listAdministrations() {
    return administrationRepo.findAll();
  }
}
