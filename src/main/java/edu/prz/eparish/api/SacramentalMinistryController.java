package edu.prz.eparish.api;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.Parafianin;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinId;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinRepozytorium;
import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.informacjeoparafii.domain.parafia.ParafiaRepozytorium;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.Ksiadz;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.KsiadzId;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.KsiadzRepozytorium;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.Sakrament;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.SakramentId;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.SakramentRepozytorium;
import edu.prz.eparish.poslugasakramentalna.domain.udzielaniesakramentu.UdzielanieSakramentu;
import edu.prz.eparish.poslugasakramentalna.domain.udzielaniesakramentu.UdzielanieSakramentuRepozytorium;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SacramentalMinistryController {

  private final KsiadzRepozytorium ksiadzRepozytorium;
  private final SakramentRepozytorium sakramentRepozytorium;
  private final UdzielanieSakramentuRepozytorium udzielanieSakramentuRepozytorium;
  private final ParafianinRepozytorium parafianinRepozytorium;
  private final ParafiaRepozytorium parafiaRepozytorium;

  @GetMapping("/priests")
  public List<PriestResponse> listPriests() {
    return ksiadzRepozytorium.findAll().stream().map(this::toPriestResponse).toList();
  }

  @GetMapping("/priests/{id}")
  public PriestResponse getPriest(@PathVariable Long id) {
    return toPriestResponse(findPriest(id));
  }

  @PostMapping("/priests")
  public ResponseEntity<PriestResponse> addPriest(@RequestBody AddPriestRequest request) {
    Parafia parafia = parafiaRepozytorium.findById(request.parishId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parafia nie istnieje"));

    Ksiadz ksiadz = new Ksiadz();
    ksiadz.setId(EntityIds.nextId(ksiadzRepozytorium, Ksiadz::getId));
    ksiadz.setImie(request.firstName());
    ksiadz.setNazwisko(request.lastName());
    ksiadz.setTelefon(request.phone());
    ksiadz.setEmail(request.email());
    ksiadz.setDataSwiecen(request.ordinationDate());
    ksiadz.setFunkcja(request.role());
    ksiadz.setParafia(parafia);

    Ksiadz saved = ksiadzRepozytorium.save(ksiadz);
    return ResponseEntity.status(HttpStatus.CREATED).body(toPriestResponse(saved));
  }

  @GetMapping("/sacraments")
  public List<SacramentResponse> listSacraments() {
    return sakramentRepozytorium.findAll().stream()
        .map(s -> new SacramentResponse(s.getId(), s.getNazwa(), s.getOpis()))
        .toList();
  }

  @PostMapping("/sacraments")
  public ResponseEntity<SacramentResponse> addSacrament(@RequestBody AddSacramentRequest request) {
    Sakrament sakrament = new Sakrament();
    sakrament.setId(EntityIds.nextId(sakramentRepozytorium, Sakrament::getId));
    sakrament.setNazwa(request.name());
    sakrament.setOpis(request.description());

    Sakrament saved = sakramentRepozytorium.save(sakrament);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new SacramentResponse(saved.getId(), saved.getNazwa(), saved.getOpis()));
  }

  @GetMapping("/sacrament-administrations")
  public List<SacramentAdministrationResponse> listSacramentAdministrations() {
    return udzielanieSakramentuRepozytorium.findAll().stream()
        .map(this::toAdministrationResponse)
        .toList();
  }

  @PostMapping("/sacrament-administrations")
  public ResponseEntity<SacramentAdministrationResponse> addSacramentAdministration(
      @RequestBody AddSacramentAdministrationRequest request) {
    requireParishioner(request.parishionerId());
    requirePriest(request.priestId());
    requireSacrament(request.sacramentId());

    UdzielanieSakramentu udzielanie = new UdzielanieSakramentu();
    udzielanie.setId(EntityIds.nextId(udzielanieSakramentuRepozytorium, UdzielanieSakramentu::getId));
    udzielanie.setDataUdzielenia(request.administrationDate());
    udzielanie.setParafianinId(new ParafianinId(request.parishionerId()));
    udzielanie.setKsiadzId(new KsiadzId(request.priestId()));
    udzielanie.setSakramentId(new SakramentId(request.sacramentId()));

    UdzielanieSakramentu saved = udzielanieSakramentuRepozytorium.save(udzielanie);
    return ResponseEntity.status(HttpStatus.CREATED).body(toAdministrationResponse(saved));
  }

  private Ksiadz findPriest(Long id) {
    return ksiadzRepozytorium.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ksiadz nie istnieje"));
  }

  private void requireParishioner(Long id) {
    if (!parafianinRepozytorium.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parafianin nie istnieje");
    }
  }

  private void requirePriest(Long id) {
    if (!ksiadzRepozytorium.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ksiadz nie istnieje");
    }
  }

  private void requireSacrament(Long id) {
    if (!sakramentRepozytorium.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sakrament nie istnieje");
    }
  }

  private PriestResponse toPriestResponse(Ksiadz ksiadz) {
    Long parishId = ksiadz.getParafia() != null ? ksiadz.getParafia().getId() : null;
    return new PriestResponse(
        ksiadz.getId(),
        ksiadz.getImie(),
        ksiadz.getNazwisko(),
        ksiadz.getTelefon(),
        ksiadz.getEmail(),
        ksiadz.getDataSwiecen(),
        ksiadz.getFunkcja(),
        parishId);
  }

  private SacramentAdministrationResponse toAdministrationResponse(UdzielanieSakramentu u) {
    return new SacramentAdministrationResponse(
        u.getId(),
        u.getDataUdzielenia(),
        u.getParafianinId().wartosc(),
        u.getKsiadzId().wartosc(),
        u.getSakramentId().wartosc());
  }

  public record AddPriestRequest(
      String firstName,
      String lastName,
      String phone,
      String email,
      LocalDate ordinationDate,
      String role,
      Long parishId) {}

  public record PriestResponse(
      Long id,
      String firstName,
      String lastName,
      String phone,
      String email,
      LocalDate ordinationDate,
      String role,
      Long parishId) {}

  public record AddSacramentRequest(String name, String description) {}

  public record SacramentResponse(Long id, String name, String description) {}

  public record AddSacramentAdministrationRequest(
      LocalDate administrationDate, Long parishionerId, Long priestId, Long sacramentId) {}

  public record SacramentAdministrationResponse(
      Long id, LocalDate administrationDate, Long parishionerId, Long priestId, Long sacramentId) {}
}
