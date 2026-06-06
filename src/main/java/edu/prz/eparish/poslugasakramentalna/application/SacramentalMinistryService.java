package edu.prz.eparish.poslugasakramentalna.application;

import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinId;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.KsiadzId;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinRepozytorium;
import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.informacjeoparafii.domain.parafia.ParafiaRepozytorium;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.Ksiadz;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.KsiadzRepozytorium;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.Sakrament;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.SakramentId;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.SakramentRepozytorium;
import edu.prz.eparish.poslugasakramentalna.domain.udzielaniesakramentu.UdzielanieSakramentu;
import edu.prz.eparish.poslugasakramentalna.domain.udzielaniesakramentu.UdzielanieSakramentuRepozytorium;
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

  public record PatchPriestCommand(
      String firstName, String lastName, String phone, String email,
      LocalDate ordinationDate, String role, Long parishId) {}

  public record PatchSacramentCommand(String name, String description) {}

  public record PatchAdministrationCommand(
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

  public Ksiadz patchPriest(Long id, PatchPriestCommand cmd) {
    Ksiadz priest = getPriest(id);
    if (cmd.firstName() != null) {
      priest.setImie(cmd.firstName());
    }
    if (cmd.lastName() != null) {
      priest.setNazwisko(cmd.lastName());
    }
    if (cmd.phone() != null) {
      priest.setTelefon(cmd.phone());
    }
    if (cmd.email() != null) {
      priest.setEmail(cmd.email());
    }
    if (cmd.ordinationDate() != null) {
      priest.setDataSwiecen(cmd.ordinationDate());
    }
    if (cmd.role() != null) {
      priest.setFunkcja(cmd.role());
    }
    if (cmd.parishId() != null) {
      Parafia parish = parishRepo.findById(cmd.parishId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found"));
      priest.setParafia(parish);
    }
    return priestRepo.save(priest);
  }

  public void deletePriest(Long id) {
    if (!priestRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Priest not found");
    }
    priestRepo.deleteById(id);
  }

  public List<Ksiadz> listPriests(Long parishId, String role) {
    return ListFilterSupport.filter(priestRepo.findAll(),
        ListFilterSupport.eqLong(parishId, k -> k.getParafia() != null ? k.getParafia().getId() : null),
        ListFilterSupport.eq(role, Ksiadz::getFunkcja));
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

  public Sakrament patchSacrament(Long id, PatchSacramentCommand cmd) {
    Sakrament sacrament = sacramentRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sacrament not found"));
    if (cmd.name() != null) {
      sacrament.setNazwa(cmd.name());
    }
    if (cmd.description() != null) {
      sacrament.setOpis(cmd.description());
    }
    return sacramentRepo.save(sacrament);
  }

  public void deleteSacrament(Long id) {
    if (!sacramentRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sacrament not found");
    }
    sacramentRepo.deleteById(id);
  }

  public List<Sakrament> listSacraments(String name) {
    return ListFilterSupport.filter(sacramentRepo.findAll(),
        ListFilterSupport.containsIgnoreCase(name, Sakrament::getNazwa));
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

  public UdzielanieSakramentu getAdministration(Long id) {
    return administrationRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Administration not found"));
  }

  public UdzielanieSakramentu patchAdministration(Long id, PatchAdministrationCommand cmd) {
    UdzielanieSakramentu administration = getAdministration(id);
    if (cmd.administrationDate() != null) {
      administration.setDataUdzielenia(cmd.administrationDate());
    }
    if (cmd.parishionerId() != null) {
      if (!parishionerRepo.existsById(cmd.parishionerId())) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner not found");
      }
      administration.setParafianinId(new ParafianinId(cmd.parishionerId()));
    }
    if (cmd.priestId() != null) {
      if (!priestRepo.existsById(cmd.priestId())) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Priest not found");
      }
      administration.setKsiadzId(new KsiadzId(cmd.priestId()));
    }
    if (cmd.sacramentId() != null) {
      if (!sacramentRepo.existsById(cmd.sacramentId())) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sacrament not found");
      }
      administration.setSakramentId(new SakramentId(cmd.sacramentId()));
    }
    return administrationRepo.save(administration);
  }

  public void deleteAdministration(Long id) {
    if (!administrationRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Administration not found");
    }
    administrationRepo.deleteById(id);
  }

  public List<UdzielanieSakramentu> listAdministrations(
      Long parishionerId, Long priestId, Long sacramentId, LocalDate administrationDate) {
    return ListFilterSupport.filter(administrationRepo.findAll(),
        ListFilterSupport.eqLong(parishionerId, a -> a.getParafianinId().wartosc()),
        ListFilterSupport.eqLong(priestId, a -> a.getKsiadzId().wartosc()),
        ListFilterSupport.eqLong(sacramentId, a -> a.getSakramentId().wartosc()),
        ListFilterSupport.eq(administrationDate, UdzielanieSakramentu::getDataUdzielenia));
  }
}
