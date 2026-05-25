package edu.prz.eparish.api;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.Parafianin;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinRepozytorium;
import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.Rodzina;
import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.RodzinaRepozytorium;
import edu.prz.eparish.informacjeoparafii.domain.diecezja.Diecezja;
import edu.prz.eparish.informacjeoparafii.domain.diecezja.DiecezjaRepozytorium;
import edu.prz.eparish.informacjeoparafii.domain.dokument.Dokument;
import edu.prz.eparish.informacjeoparafii.domain.dokument.DokumentRepozytorium;
import edu.prz.eparish.informacjeoparafii.domain.kartoteka.Kartoteka;
import edu.prz.eparish.informacjeoparafii.domain.kartoteka.KartotekaRepozytorium;
import edu.prz.eparish.informacjeoparafii.domain.miejscowosc.Miejscowosc;
import edu.prz.eparish.informacjeoparafii.domain.miejscowosc.MiejscowoscRepozytorium;
import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.informacjeoparafii.domain.parafia.ParafiaRepozytorium;
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
public class ParishInformationController {

  private final DiecezjaRepozytorium diecezjaRepozytorium;
  private final MiejscowoscRepozytorium miejscowoscRepozytorium;
  private final ParafiaRepozytorium parafiaRepozytorium;
  private final ParafianinRepozytorium parafianinRepozytorium;
  private final RodzinaRepozytorium rodzinaRepozytorium;
  private final KartotekaRepozytorium kartotekaRepozytorium;
  private final DokumentRepozytorium dokumentRepozytorium;

  @GetMapping("/dioceses")
  public List<DioceseResponse> listDioceses() {
    return diecezjaRepozytorium.findAll().stream()
        .map(d -> new DioceseResponse(d.getId(), d.getNazwa(), d.getSiedziba(), d.getBiskup()))
        .toList();
  }

  @PostMapping("/dioceses")
  public ResponseEntity<DioceseResponse> addDiocese(@RequestBody AddDioceseRequest request) {
    Diecezja diecezja = new Diecezja();
    diecezja.setId(EntityIds.nextId(diecezjaRepozytorium, Diecezja::getId));
    diecezja.setNazwa(request.name());
    diecezja.setSiedziba(request.see());
    diecezja.setBiskup(request.bishop());

    Diecezja saved = diecezjaRepozytorium.save(diecezja);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new DioceseResponse(saved.getId(), saved.getNazwa(), saved.getSiedziba(), saved.getBiskup()));
  }

  @GetMapping("/localities")
  public List<LocalityResponse> listLocalities() {
    return miejscowoscRepozytorium.findAll().stream().map(this::toLocalityResponse).toList();
  }

  @PostMapping("/localities")
  public ResponseEntity<LocalityResponse> addLocality(@RequestBody AddLocalityRequest request) {
    Diecezja diecezja = diecezjaRepozytorium.findById(request.dioceseId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diecezja nie istnieje"));

    Miejscowosc miejscowosc = new Miejscowosc();
    miejscowosc.setId(EntityIds.nextId(miejscowoscRepozytorium, Miejscowosc::getId));
    miejscowosc.setNazwa(request.name());
    miejscowosc.setKodPocztowy(request.postalCode());
    miejscowosc.setWojewodztwo(request.province());
    miejscowosc.setDiecezja(diecezja);

    Miejscowosc saved = miejscowoscRepozytorium.save(miejscowosc);
    return ResponseEntity.status(HttpStatus.CREATED).body(toLocalityResponse(saved));
  }

  @GetMapping("/parishes")
  public List<ParishResponse> listParishes() {
    return parafiaRepozytorium.findAll().stream().map(this::toParishResponse).toList();
  }

  @GetMapping("/parishes/{id}")
  public ParishResponse getParish(@PathVariable Long id) {
    return toParishResponse(findParish(id));
  }

  @GetMapping("/parishioners")
  public List<ParishionerResponse> listParishioners() {
    return parafianinRepozytorium.findAll().stream().map(this::toParishionerResponse).toList();
  }

  @GetMapping("/parishioners/{id}")
  public ParishionerResponse getParishioner(@PathVariable Long id) {
    return toParishionerResponse(findParishioner(id));
  }

  @PostMapping("/parishioners")
  public ResponseEntity<ParishionerResponse> addParishioner(@RequestBody AddParishionerRequest request) {
    Parafia parafia = parafiaRepozytorium.findById(request.parishId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parafia nie istnieje"));
    Rodzina rodzina = null;
    if (request.familyId() != null) {
      rodzina = rodzinaRepozytorium.findById(request.familyId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rodzina nie istnieje"));
    }

    Parafianin parafianin = new Parafianin();
    parafianin.setId(EntityIds.nextId(parafianinRepozytorium, Parafianin::getId));
    parafianin.setImie(request.firstName());
    parafianin.setNazwisko(request.lastName());
    parafianin.setPesel(request.pesel());
    parafianin.setDataUrodzenia(request.birthDate());
    parafianin.setTelefon(request.phone());
    parafianin.setEmail(request.email());
    parafianin.setParafia(parafia);
    parafianin.setRodzina(rodzina);

    Parafianin saved = parafianinRepozytorium.save(parafianin);
    return ResponseEntity.status(HttpStatus.CREATED).body(toParishionerResponse(saved));
  }

  @GetMapping("/records")
  public List<RecordResponse> listRecords() {
    return kartotekaRepozytorium.findAll().stream().map(this::toRecordResponse).toList();
  }

  @PostMapping("/records")
  public ResponseEntity<RecordResponse> addRecord(@RequestBody AddRecordRequest request) {
    Parafianin parafianin = findParishioner(request.parishionerId());

    Kartoteka kartoteka = new Kartoteka();
    kartoteka.setId(EntityIds.nextId(kartotekaRepozytorium, Kartoteka::getId));
    kartoteka.setDataUtworzenia(request.createdAt());
    kartoteka.setOpis(request.description());
    kartoteka.setParafianin(parafianin);

    Kartoteka saved = kartotekaRepozytorium.save(kartoteka);
    return ResponseEntity.status(HttpStatus.CREATED).body(toRecordResponse(saved));
  }

  @GetMapping("/documents")
  public List<DocumentResponse> listDocuments() {
    return dokumentRepozytorium.findAll().stream().map(this::toDocumentResponse).toList();
  }

  @PostMapping("/documents")
  public ResponseEntity<DocumentResponse> addDocument(@RequestBody AddDocumentRequest request) {
    Kartoteka kartoteka = kartotekaRepozytorium.findById(request.recordId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kartoteka nie istnieje"));

    Dokument dokument = new Dokument();
    dokument.setId(EntityIds.nextId(dokumentRepozytorium, Dokument::getId));
    dokument.setTyp(request.type());
    dokument.setDataWystawienia(request.issueDate());
    dokument.setOpis(request.description());
    dokument.setKartoteka(kartoteka);

    Dokument saved = dokumentRepozytorium.save(dokument);
    return ResponseEntity.status(HttpStatus.CREATED).body(toDocumentResponse(saved));
  }

  private Parafia findParish(Long id) {
    return parafiaRepozytorium.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parafia nie istnieje"));
  }

  private Parafianin findParishioner(Long id) {
    return parafianinRepozytorium.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parafianin nie istnieje"));
  }

  private LocalityResponse toLocalityResponse(Miejscowosc m) {
    Long dioceseId = m.getDiecezja() != null ? m.getDiecezja().getId() : null;
    return new LocalityResponse(m.getId(), m.getNazwa(), m.getKodPocztowy(), m.getWojewodztwo(), dioceseId);
  }

  private ParishResponse toParishResponse(Parafia p) {
    Long localityId = p.getMiejscowosc() != null ? p.getMiejscowosc().getId() : null;
    return new ParishResponse(
        p.getId(), p.getNazwa(), p.getAdres(), p.getTelefon(), p.getEmail(), p.getDataErygowania(), localityId);
  }

  private ParishionerResponse toParishionerResponse(Parafianin p) {
    Long parishId = p.getParafia() != null ? p.getParafia().getId() : null;
    Long familyId = p.getRodzina() != null ? p.getRodzina().getId() : null;
    return new ParishionerResponse(
        p.getId(),
        p.getImie(),
        p.getNazwisko(),
        p.getPesel(),
        p.getDataUrodzenia(),
        p.getTelefon(),
        p.getEmail(),
        parishId,
        familyId);
  }

  private RecordResponse toRecordResponse(Kartoteka k) {
    Long parishionerId = k.getParafianin() != null ? k.getParafianin().getId() : null;
    return new RecordResponse(k.getId(), k.getDataUtworzenia(), k.getOpis(), parishionerId);
  }

  private DocumentResponse toDocumentResponse(Dokument d) {
    Long recordId = d.getKartoteka() != null ? d.getKartoteka().getId() : null;
    return new DocumentResponse(d.getId(), d.getTyp(), d.getDataWystawienia(), d.getOpis(), recordId);
  }

  public record AddDioceseRequest(String name, String see, String bishop) {}

  public record DioceseResponse(Long id, String name, String see, String bishop) {}

  public record AddLocalityRequest(String name, String postalCode, String province, Long dioceseId) {}

  public record LocalityResponse(Long id, String name, String postalCode, String province, Long dioceseId) {}

  public record ParishResponse(
      Long id,
      String name,
      String address,
      String phone,
      String email,
      LocalDate erectionDate,
      Long localityId) {}

  public record AddParishionerRequest(
      String firstName,
      String lastName,
      String pesel,
      LocalDate birthDate,
      String phone,
      String email,
      Long parishId,
      Long familyId) {}

  public record ParishionerResponse(
      Long id,
      String firstName,
      String lastName,
      String pesel,
      LocalDate birthDate,
      String phone,
      String email,
      Long parishId,
      Long familyId) {}

  public record AddRecordRequest(LocalDate createdAt, String description, Long parishionerId) {}

  public record RecordResponse(Long id, LocalDate createdAt, String description, Long parishionerId) {}

  public record AddDocumentRequest(String type, LocalDate issueDate, String description, Long recordId) {}

  public record DocumentResponse(Long id, String type, LocalDate issueDate, String description, Long recordId) {}
}
