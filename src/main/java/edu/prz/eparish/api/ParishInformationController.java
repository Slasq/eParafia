package edu.prz.eparish.api;

import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.Parafianin;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinAgregat;
import edu.prz.eparish.informacjeoparafii.application.ParishInformationService;
import edu.prz.eparish.informacjeoparafii.application.ParishInformationService.AddDioceseCommand;
import edu.prz.eparish.informacjeoparafii.application.ParishInformationService.AddDocumentCommand;
import edu.prz.eparish.informacjeoparafii.application.ParishInformationService.AddLocalityCommand;
import edu.prz.eparish.informacjeoparafii.application.ParishInformationService.AddParishCommand;
import edu.prz.eparish.informacjeoparafii.application.ParishInformationService.CreateRecordCommand;
import edu.prz.eparish.informacjeoparafii.application.ParishInformationService.RegisterParishionerCommand;
import edu.prz.eparish.informacjeoparafii.domain.diecezja.Diecezja;
import edu.prz.eparish.informacjeoparafii.domain.dokument.Dokument;
import edu.prz.eparish.informacjeoparafii.domain.kartoteka.Kartoteka;
import edu.prz.eparish.informacjeoparafii.domain.miejscowosc.Miejscowosc;
import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@Tag(name = "Parish Information", description = "Parish, diocese, parishioners, records — ParafianinAgregat")
public class ParishInformationController {

  private final ParishInformationService service;

  // ── DIOCESES — UC: Dodaj diecezję ───────────────────────────────────────────

  @GetMapping("/dioceses")
  @Operation(summary = "List all dioceses")
  public List<DioceseResponse> listDioceses() {
    return service.listDioceses().stream().map(this::toDioceseResponse).toList();
  }

  @PostMapping("/dioceses")
  @Operation(summary = "UC: Dodaj diecezję — create diocese")
  public ResponseEntity<DioceseResponse> addDiocese(@RequestBody AddDioceseRequest req) {
    Diecezja d = service.addDiocese(new AddDioceseCommand(req.name(), req.see(), req.bishop()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toDioceseResponse(d));
  }

  @PutMapping("/dioceses/{id}")
  @Operation(summary = "UC: Dodaj biskupa / siedzibę / zmień nazwę — update diocese")
  public DioceseResponse updateDiocese(@PathVariable Long id, @RequestBody AddDioceseRequest req) {
    return toDioceseResponse(service.updateDiocese(id, new AddDioceseCommand(req.name(), req.see(), req.bishop())));
  }

  // ── LOCALITIES — UC: Dodaj miejscowość ──────────────────────────────────────

  @GetMapping("/localities")
  @Operation(summary = "List all localities")
  public List<LocalityResponse> listLocalities() {
    return service.listLocalities().stream().map(this::toLocalityResponse).toList();
  }

  @PostMapping("/localities")
  @Operation(summary = "UC: Dodaj miejscowość — create locality")
  public ResponseEntity<LocalityResponse> addLocality(@RequestBody AddLocalityRequest req) {
    Miejscowosc m = service.addLocality(new AddLocalityCommand(
        req.name(), req.postalCode(), req.province(), req.dioceseId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toLocalityResponse(m));
  }

  // ── PARISHES — UC: Zarządzanie parafią ──────────────────────────────────────

  @GetMapping("/parishes")
  @Operation(summary = "List all parishes")
  public List<ParishResponse> listParishes() {
    return service.listParishes().stream().map(this::toParishResponse).toList();
  }

  @GetMapping("/parishes/{id}")
  @Operation(summary = "Get parish by ID")
  public ParishResponse getParish(@PathVariable Long id) {
    return toParishResponse(service.getParish(id));
  }

  @PostMapping("/parishes")
  @Operation(summary = "UC: Zarządzanie parafią — create parish")
  public ResponseEntity<ParishResponse> addParish(@RequestBody AddParishRequest req) {
    Parafia p = service.addParish(new AddParishCommand(
        req.name(), req.address(), req.phone(), req.email(), req.erectionDate(), req.localityId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toParishResponse(p));
  }

  @PutMapping("/parishes/{id}")
  @Operation(summary = "Update parish")
  public ParishResponse updateParish(@PathVariable Long id, @RequestBody AddParishRequest req) {
    return toParishResponse(service.updateParish(id, new AddParishCommand(
        req.name(), req.address(), req.phone(), req.email(), req.erectionDate(), req.localityId())));
  }

  // ── PARISHIONERS — UC: Zarządzanie parafianami ──────────────────────────────

  @GetMapping("/parishioners")
  @Operation(summary = "List all parishioners")
  public List<ParishionerResponse> listParishioners() {
    return service.listParishioners().stream().map(this::toParishionerResponse).toList();
  }

  @GetMapping("/parishioners/{id}")
  @Operation(summary = "Get parishioner by ID")
  public ParishionerResponse getParishioner(@PathVariable Long id) {
    return toParishionerResponse(service.getParishioner(id));
  }

  @PostMapping("/parishioners")
  @Operation(summary = "UC: Zarządzanie parafianami — register parishioner")
  public ResponseEntity<ParishionerResponse> addParishioner(@RequestBody AddParishionerRequest req) {
    Parafianin p = service.registerParishioner(new RegisterParishionerCommand(
        req.firstName(), req.lastName(), req.pesel(), req.birthDate(),
        req.phone(), req.email(), req.parishId(), req.familyId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toParishionerResponse(p));
  }

  @PutMapping("/parishioners/{id}")
  @Operation(summary = "Update parishioner data")
  public ParishionerResponse updateParishioner(
      @PathVariable Long id, @RequestBody AddParishionerRequest req) {
    return toParishionerResponse(service.updateParishioner(id, new RegisterParishionerCommand(
        req.firstName(), req.lastName(), req.pesel(), req.birthDate(),
        req.phone(), req.email(), req.parishId(), req.familyId())));
  }

  @DeleteMapping("/parishioners/{id}")
  @Operation(summary = "Remove parishioner")
  public ResponseEntity<Void> deleteParishioner(@PathVariable Long id) {
    service.removeParishioner(id);
    return ResponseEntity.noContent().build();
  }

  // ── PARISHIONER AGGREGATE — ParafianinAgregat ────────────────────────────────

  @GetMapping("/parishioners/{id}/aggregate")
  @Operation(summary = "UC: ParafianinAgregat — parishioner with record and documents",
      description = "Returns parishioner with associated record and all documents. "
          + "Covers UC: prowadzenie kartotek, zarządzanie dokumentacją, rejestracja zdarzeń.")
  public ParishionerAggregateResponse getParishionerAggregate(@PathVariable Long id) {
    ParafianinAgregat agg = service.getParishionerAggregate(id);
    return toAggregateResponse(agg);
  }

  // ── RECORDS — UC: Prowadzenie kartotek parafian ──────────────────────────────

  @GetMapping("/records")
  @Operation(summary = "List all records")
  public List<RecordResponse> listRecords() {
    return service.listRecords().stream().map(this::toRecordResponse).toList();
  }

  @PostMapping("/records")
  @Operation(summary = "UC: Prowadzenie kartotek — create record directly")
  public ResponseEntity<RecordResponse> addRecord(@RequestBody AddRecordRequest req) {
    Kartoteka k = service.createRecordDirect(
        new CreateRecordCommand(req.createdAt(), req.description(), req.parishionerId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toRecordResponse(k));
  }

  @PostMapping("/parishioners/{id}/record")
  @Operation(summary = "UC: Prowadzenie kartotek — create record for parishioner")
  public ResponseEntity<RecordResponse> createRecordForParishioner(
      @PathVariable Long id, @RequestBody AddRecordRequest req) {
    Kartoteka k = service.createRecord(id, new CreateRecordCommand(req.createdAt(), req.description(), id));
    return ResponseEntity.status(HttpStatus.CREATED).body(toRecordResponse(k));
  }

  @PutMapping("/records/{id}")
  @Operation(summary = "UC: Rejestracja zdarzeń religijnych — update record description")
  public RecordResponse updateRecord(@PathVariable Long id, @RequestBody UpdateRecordRequest req) {
    return toRecordResponse(service.updateRecord(id, req.description()));
  }

  @DeleteMapping("/records/{id}")
  @Operation(summary = "Delete record")
  public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
    service.deleteRecord(id);
    return ResponseEntity.noContent().build();
  }

  // ── DOCUMENTS — UC: Zarządzanie dokumentacją ────────────────────────────────

  @GetMapping("/documents")
  @Operation(summary = "List all documents")
  public List<DocumentResponse> listDocuments() {
    return service.listDocuments().stream().map(this::toDocumentResponse).toList();
  }

  @PostMapping("/parishioners/{id}/record/documents")
  @Operation(summary = "UC: Zarządzanie dokumentacją — add document to parishioner record")
  public ResponseEntity<DocumentResponse> addDocumentToParishioner(
      @PathVariable Long id, @RequestBody AddDocumentRequest req) {
    Dokument d = service.addDocumentToParishioner(id,
        new AddDocumentCommand(req.type(), req.issueDate(), req.description(), null));
    return ResponseEntity.status(HttpStatus.CREATED).body(toDocumentResponse(d));
  }

  @PostMapping("/documents")
  @Operation(summary = "UC: Zarządzanie dokumentacją — add document directly to record")
  public ResponseEntity<DocumentResponse> addDocument(@RequestBody AddDocumentRequest req) {
    Dokument d = service.addDocumentToRecord(req.recordId(),
        new AddDocumentCommand(req.type(), req.issueDate(), req.description(), req.recordId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toDocumentResponse(d));
  }

  @PutMapping("/documents/{id}")
  @Operation(summary = "Update document")
  public DocumentResponse updateDocument(@PathVariable Long id, @RequestBody UpdateDocumentRequest req) {
    return toDocumentResponse(service.updateDocument(id, req.type(), req.issueDate(), req.description()));
  }

  @DeleteMapping("/documents/{id}")
  @Operation(summary = "Delete document")
  public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
    service.deleteDocument(id);
    return ResponseEntity.noContent().build();
  }

  // ── Mapping helpers ──────────────────────────────────────────────────────────

  private DioceseResponse toDioceseResponse(Diecezja d) {
    return new DioceseResponse(d.getId(), d.getNazwa(), d.getSiedziba(), d.getBiskup());
  }

  private LocalityResponse toLocalityResponse(Miejscowosc m) {
    Long dioceseId = m.getDiecezja() != null ? m.getDiecezja().getId() : null;
    return new LocalityResponse(m.getId(), m.getNazwa(), m.getKodPocztowy(), m.getWojewodztwo(), dioceseId);
  }

  private ParishResponse toParishResponse(Parafia p) {
    Long localityId = p.getMiejscowosc() != null ? p.getMiejscowosc().getId() : null;
    return new ParishResponse(p.getId(), p.getNazwa(), p.getAdres(), p.getTelefon(),
        p.getEmail(), p.getDataErygowania(), localityId);
  }

  private ParishionerResponse toParishionerResponse(Parafianin p) {
    Long parishId = p.getParafia() != null ? p.getParafia().getId() : null;
    Long familyId = p.getRodzina() != null ? p.getRodzina().getId() : null;
    return new ParishionerResponse(p.getId(), p.getImie(), p.getNazwisko(), p.getPesel(),
        p.getDataUrodzenia(), p.getTelefon(), p.getEmail(), parishId, familyId);
  }

  private RecordResponse toRecordResponse(Kartoteka k) {
    Long parishionerId = k.getParafianin() != null ? k.getParafianin().getId() : null;
    return new RecordResponse(k.getId(), k.getDataUtworzenia(), k.getOpis(), parishionerId);
  }

  private DocumentResponse toDocumentResponse(Dokument d) {
    Long recordId = d.getKartoteka() != null ? d.getKartoteka().getId() : null;
    return new DocumentResponse(d.getId(), d.getTyp(), d.getDataWystawienia(), d.getOpis(), recordId);
  }

  private ParishionerAggregateResponse toAggregateResponse(ParafianinAgregat a) {
    RecordResponse rec = a.getKartoteka() != null ? toRecordResponse(a.getKartoteka()) : null;
    return new ParishionerAggregateResponse(
        toParishionerResponse(a.getRoot()),
        a.hasRecord(),
        a.isProfileComplete(),
        a.documentCount(),
        rec,
        a.getDokumenty().stream().map(this::toDocumentResponse).toList());
  }

  // ── Request / Response records ───────────────────────────────────────────────

  public record AddDioceseRequest(String name, String see, String bishop) {}
  public record DioceseResponse(Long id, String name, String see, String bishop) {}

  public record AddLocalityRequest(String name, String postalCode, String province, Long dioceseId) {}
  public record LocalityResponse(Long id, String name, String postalCode, String province, Long dioceseId) {}

  public record AddParishRequest(String name, String address, String phone, String email,
      LocalDate erectionDate, Long localityId) {}
  public record ParishResponse(Long id, String name, String address, String phone, String email,
      LocalDate erectionDate, Long localityId) {}

  public record AddParishionerRequest(String firstName, String lastName, String pesel,
      LocalDate birthDate, String phone, String email, Long parishId, Long familyId) {}
  public record ParishionerResponse(Long id, String firstName, String lastName, String pesel,
      LocalDate birthDate, String phone, String email, Long parishId, Long familyId) {}

  public record ParishionerAggregateResponse(ParishionerResponse parishioner, boolean hasRecord,
      boolean profileComplete, int documentCount, RecordResponse record, List<DocumentResponse> documents) {}

  public record AddRecordRequest(LocalDate createdAt, String description, Long parishionerId) {}
  public record UpdateRecordRequest(String description) {}
  public record RecordResponse(Long id, LocalDate createdAt, String description, Long parishionerId) {}

  public record AddDocumentRequest(String type, LocalDate issueDate, String description, Long recordId) {}
  public record UpdateDocumentRequest(String type, LocalDate issueDate, String description) {}
  public record DocumentResponse(Long id, String type, LocalDate issueDate, String description, Long recordId) {}
}
