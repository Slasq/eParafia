package edu.prz.eparish.api;

import edu.prz.eparish.pastoralcare.domain.parishioner.Parishioner;
import edu.prz.eparish.pastoralcare.domain.parishioner.ParishionerAggregate;
import edu.prz.eparish.parishinformation.application.ParishInformationService;
import edu.prz.eparish.parishinformation.application.ParishInformationService.AddDioceseCommand;
import edu.prz.eparish.parishinformation.application.ParishInformationService.AddDocumentCommand;
import edu.prz.eparish.parishinformation.application.ParishInformationService.AddLocalityCommand;
import edu.prz.eparish.parishinformation.application.ParishInformationService.AddParishCommand;
import edu.prz.eparish.parishinformation.application.ParishInformationService.CreateRecordCommand;
import edu.prz.eparish.parishinformation.application.ParishInformationService.RegisterParishionerCommand;
import edu.prz.eparish.parishinformation.domain.diocese.Diocese;
import edu.prz.eparish.parishinformation.domain.document.Document;
import edu.prz.eparish.parishinformation.domain.record.ParishRecord;
import edu.prz.eparish.parishinformation.domain.locality.Locality;
import edu.prz.eparish.parishinformation.domain.parish.Parish;
import com.fasterxml.jackson.annotation.JsonAlias;
import edu.prz.eparish.api.support.PatchBodySupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
@Tag(name = "Parish Information", description = "Parish, diocese, parishioners, records — ParishionerAggregate")
public class ParishInformationController {

  private final ParishInformationService service;

  // ── DIOCESES — UC: Dodaj diecezję ───────────────────────────────────────────

  @GetMapping("/dioceses")
  @Operation(summary = "List dioceses (optional filters: name, see, bishop)")
  public List<DioceseResponse> listDioceses(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String see,
      @RequestParam(required = false) String bishop) {
    return service.listDioceses(name, see, bishop).stream().map(this::toDioceseResponse).toList();
  }

  @GetMapping("/dioceses/{id}")
  @Operation(summary = "Get diocese by ID")
  public DioceseResponse getDiocese(@PathVariable Long id) {
    return toDioceseResponse(service.getDiocese(id));
  }

  @PostMapping("/dioceses")
  @Operation(summary = "UC: Dodaj diecezję — create diocese")
  public ResponseEntity<DioceseResponse> addDiocese(@RequestBody AddDioceseRequest req) {
    Diocese d = service.addDiocese(new AddDioceseCommand(req.name(), req.see(), req.bishop()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toDioceseResponse(d));
  }

  @PutMapping("/dioceses/{id}")
  @Operation(summary = "UC: Dodaj biskupa / siedzibę / zmień nazwę — update diocese")
  public DioceseResponse updateDiocese(@PathVariable Long id, @RequestBody AddDioceseRequest req) {
    return toDioceseResponse(service.updateDiocese(id, new AddDioceseCommand(req.name(), req.see(), req.bishop())));
  }

  @PatchMapping("/dioceses/{id}")
  @Operation(summary = "Partially update diocese")
  public DioceseResponse patchDiocese(@PathVariable Long id, @RequestBody PatchDioceseRequest req) {
    return toDioceseResponse(service.patchDiocese(id,
        new ParishInformationService.PatchDioceseCommand(req.name(), req.see(), req.bishop())));
  }

  @DeleteMapping("/dioceses/{id}")
  @Operation(summary = "Delete diocese")
  public ResponseEntity<Void> deleteDiocese(@PathVariable Long id) {
    service.deleteDiocese(id);
    return ResponseEntity.noContent().build();
  }

  // ── LOCALITIES — UC: Dodaj miejscowość ──────────────────────────────────────

  @GetMapping("/localities")
  @Operation(summary = "List localities (optional filters: dioceseId, province, postalCode, name)")
  public List<LocalityResponse> listLocalities(
      @RequestParam(required = false) Long dioceseId,
      @RequestParam(required = false) String province,
      @RequestParam(required = false) String postalCode,
      @RequestParam(required = false) String name) {
    return service.listLocalities(dioceseId, province, postalCode, name).stream()
        .map(this::toLocalityResponse).toList();
  }

  @PostMapping("/localities")
  @Operation(summary = "UC: Dodaj miejscowość — create locality")
  public ResponseEntity<LocalityResponse> addLocality(@RequestBody AddLocalityRequest req) {
    Locality m = service.addLocality(new AddLocalityCommand(
        req.name(), req.postalCode(), req.province(), req.dioceseId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toLocalityResponse(m));
  }

  @PutMapping("/localities/{id}")
  @Operation(summary = "Update locality")
  public LocalityResponse updateLocality(@PathVariable Long id, @RequestBody AddLocalityRequest req) {
    return toLocalityResponse(service.updateLocality(id, new AddLocalityCommand(
        req.name(), req.postalCode(), req.province(), req.dioceseId())));
  }

  @PatchMapping("/localities/{id}")
  @Operation(summary = "Partially update locality")
  public LocalityResponse patchLocality(@PathVariable Long id, @RequestBody PatchLocalityRequest req) {
    return toLocalityResponse(service.patchLocality(id,
        new ParishInformationService.PatchLocalityCommand(
            req.name(), req.postalCode(), req.province(), req.dioceseId())));
  }

  @DeleteMapping("/localities/{id}")
  @Operation(summary = "Delete locality")
  public ResponseEntity<Void> deleteLocality(@PathVariable Long id) {
    service.deleteLocality(id);
    return ResponseEntity.noContent().build();
  }

  // ── PARISHES — UC: Zarządzanie parafią ──────────────────────────────────────

  @GetMapping("/parishes")
  @Operation(summary = "List parishes (optional filters: localityId, name)")
  public List<ParishResponse> listParishes(
      @RequestParam(required = false) Long localityId,
      @RequestParam(required = false) String name) {
    return service.listParishes(localityId, name).stream().map(this::toParishResponse).toList();
  }

  @GetMapping("/parishes/{id}")
  @Operation(summary = "Get parish by ID")
  public ParishResponse getParish(@PathVariable Long id) {
    return toParishResponse(service.getParish(id));
  }

  @PostMapping("/parishes")
  @Operation(summary = "UC: Zarządzanie parafią — create parish")
  public ResponseEntity<ParishResponse> addParish(@RequestBody AddParishRequest req) {
    Parish p = service.addParish(new AddParishCommand(
        req.name(), req.address(), req.phone(), req.email(), req.erectionDate(), req.localityId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toParishResponse(p));
  }

  @PutMapping("/parishes/{id}")
  @Operation(summary = "Update parish")
  public ParishResponse updateParish(@PathVariable Long id, @RequestBody AddParishRequest req) {
    return toParishResponse(service.updateParish(id, new AddParishCommand(
        req.name(), req.address(), req.phone(), req.email(), req.erectionDate(), req.localityId())));
  }

  @PatchMapping("/parishes/{id}")
  @Operation(summary = "Partially update parish")
  public ParishResponse patchParish(@PathVariable Long id, @RequestBody Map<String, Object> body) {
    PatchParishRequest req = PatchBodySupport.toValue(body, PatchParishRequest.class);
    boolean localityPresent = PatchBodySupport.hasAnyField(body, "localityId", "localitiesId");
    Long localityId = localityPresent
        ? PatchBodySupport.nullableLong(body, body.containsKey("localityId") ? "localityId" : "localitiesId")
        : null;
    return toParishResponse(service.patchParish(id, new ParishInformationService.PatchParishCommand(
        req.name(), req.address(), req.phone(), req.email(), req.erectionDate(),
        localityPresent, localityId)));
  }

  @DeleteMapping("/parishes/{id}")
  @Operation(summary = "Delete parish")
  public ResponseEntity<Void> deleteParish(@PathVariable Long id) {
    service.deleteParish(id);
    return ResponseEntity.noContent().build();
  }

  // ── PARISHIONERS — UC: Zarządzanie parafianami ──────────────────────────────

  @GetMapping("/parishioners")
  @Operation(summary = "List parishioners (optional filters: parishId, familyId, pesel, firstName, lastName)")
  public List<ParishionerResponse> listParishioners(
      @RequestParam(required = false) Long parishId,
      @RequestParam(required = false) Long familyId,
      @RequestParam(required = false) String pesel,
      @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String lastName) {
    return service.listParishioners(parishId, familyId, pesel, firstName, lastName).stream()
        .map(this::toParishionerResponse).toList();
  }

  @GetMapping("/parishioners/{id}")
  @Operation(summary = "Get parishioner by ID")
  public ParishionerResponse getParishioner(@PathVariable Long id) {
    return toParishionerResponse(service.getParishioner(id));
  }

  @PostMapping("/parishioners")
  @Operation(summary = "UC: Zarządzanie parafianami — register parishioner")
  public ResponseEntity<ParishionerResponse> addParishioner(@RequestBody AddParishionerRequest req) {
    Parishioner p = service.registerParishioner(new RegisterParishionerCommand(
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

  @PatchMapping("/parishioners/{id}")
  @Operation(summary = "Partially update parishioner")
  public ParishionerResponse patchParishioner(@PathVariable Long id, @RequestBody Map<String, Object> body) {
    PatchParishionerRequest req = PatchBodySupport.toValue(body, PatchParishionerRequest.class);
    boolean familyPresent = PatchBodySupport.hasAnyField(body, "familyId");
    Long familyId = familyPresent ? PatchBodySupport.nullableLong(body, "familyId") : null;
    return toParishionerResponse(service.patchParishioner(id,
        new ParishInformationService.PatchParishionerCommand(
            req.firstName(), req.lastName(), req.pesel(), req.birthDate(),
            req.phone(), req.email(), req.parishId(), familyPresent, familyId)));
  }

  @DeleteMapping("/parishioners/{id}")
  @Operation(summary = "Remove parishioner")
  public ResponseEntity<Void> deleteParishioner(@PathVariable Long id) {
    service.removeParishioner(id);
    return ResponseEntity.noContent().build();
  }

  // ── PARISHIONER AGGREGATE — ParishionerAggregate ─────────────────────────────

  @GetMapping("/parishioners/{id}/aggregate")
  @Operation(summary = "UC: ParishionerAggregate — parishioner with record and documents",
      description = "Returns parishioner with associated record and all documents. "
          + "Covers UC: prowadzenie kartotek, zarządzanie dokumentacją, rejestracja zdarzeń.")
  public ParishionerAggregateResponse getParishionerAggregate(@PathVariable Long id) {
    ParishionerAggregate agg = service.getParishionerAggregate(id);
    return toAggregateResponse(agg);
  }

  // ── RECORDS — UC: Prowadzenie kartotek parafian ──────────────────────────────

  @GetMapping("/records")
  @Operation(summary = "List records (optional filter: parishionerId)")
  public List<RecordResponse> listRecords(@RequestParam(required = false) Long parishionerId) {
    return service.listRecords(parishionerId).stream().map(this::toRecordResponse).toList();
  }

  @PostMapping("/records")
  @Operation(summary = "UC: Prowadzenie kartotek — create record directly")
  public ResponseEntity<RecordResponse> addRecord(@RequestBody AddRecordRequest req) {
    ParishRecord k = service.createRecordDirect(
        new CreateRecordCommand(req.createdAt(), req.description(), req.parishionerId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toRecordResponse(k));
  }

  @PostMapping("/parishioners/{id}/record")
  @Operation(summary = "UC: Prowadzenie kartotek — create record for parishioner")
  public ResponseEntity<RecordResponse> createRecordForParishioner(
      @PathVariable Long id, @RequestBody AddRecordRequest req) {
    ParishRecord k = service.createRecord(id, new CreateRecordCommand(req.createdAt(), req.description(), id));
    return ResponseEntity.status(HttpStatus.CREATED).body(toRecordResponse(k));
  }

  @PutMapping("/records/{id}")
  @Operation(summary = "UC: Rejestracja zdarzeń religijnych — update record description")
  public RecordResponse updateRecord(@PathVariable Long id, @RequestBody UpdateRecordRequest req) {
    return toRecordResponse(service.updateRecord(id, req.description()));
  }

  @PatchMapping("/records/{id}")
  @Operation(summary = "Partially update record")
  public RecordResponse patchRecord(@PathVariable Long id, @RequestBody PatchRecordRequest req) {
    return toRecordResponse(service.patchRecord(id,
        new ParishInformationService.PatchRecordCommand(req.description())));
  }

  @DeleteMapping("/records/{id}")
  @Operation(summary = "Delete record")
  public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
    service.deleteRecord(id);
    return ResponseEntity.noContent().build();
  }

  // ── DOCUMENTS — UC: Zarządzanie dokumentacją ────────────────────────────────

  @GetMapping("/documents/{id}")
  @Operation(summary = "Get document by ID")
  public DocumentResponse getDocument(@PathVariable Long id) {
    return toDocumentResponse(service.getDocument(id));
  }

  @GetMapping("/documents")
  @Operation(summary = "List documents (optional filters: recordId, type)")
  public List<DocumentResponse> listDocuments(
      @RequestParam(required = false) Long recordId,
      @RequestParam(required = false) String type) {
    return service.listDocuments(recordId, type).stream().map(this::toDocumentResponse).toList();
  }

  @PostMapping("/parishioners/{id}/record/documents")
  @Operation(summary = "UC: Zarządzanie dokumentacją — add document to parishioner record")
  public ResponseEntity<DocumentResponse> addDocumentToParishioner(
      @PathVariable Long id, @RequestBody AddDocumentRequest req) {
    Document d = service.addDocumentToParishioner(id,
        new AddDocumentCommand(req.type(), req.issueDate(), req.description(), null));
    return ResponseEntity.status(HttpStatus.CREATED).body(toDocumentResponse(d));
  }

  @PostMapping("/documents")
  @Operation(summary = "UC: Zarządzanie dokumentacją — add document directly to record")
  public ResponseEntity<DocumentResponse> addDocument(@RequestBody AddDocumentRequest req) {
    Document d = service.addDocumentToRecord(req.recordId(),
        new AddDocumentCommand(req.type(), req.issueDate(), req.description(), req.recordId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toDocumentResponse(d));
  }

  @PutMapping("/documents/{id}")
  @Operation(summary = "Update document")
  public DocumentResponse updateDocument(@PathVariable Long id, @RequestBody UpdateDocumentRequest req) {
    return toDocumentResponse(service.updateDocument(id, req.type(), req.issueDate(), req.description()));
  }

  @PatchMapping("/documents/{id}")
  @Operation(summary = "Partially update document")
  public DocumentResponse patchDocument(@PathVariable Long id, @RequestBody PatchDocumentRequest req) {
    return toDocumentResponse(service.patchDocument(id,
        new ParishInformationService.PatchDocumentCommand(req.type(), req.issueDate(), req.description())));
  }

  @DeleteMapping("/documents/{id}")
  @Operation(summary = "Delete document")
  public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
    service.deleteDocument(id);
    return ResponseEntity.noContent().build();
  }

  // ── Mapping helpers ──────────────────────────────────────────────────────────

  private DioceseResponse toDioceseResponse(Diocese d) {
    return new DioceseResponse(d.getId(), d.getName(), d.getSee(), d.getBishop());
  }

  private LocalityResponse toLocalityResponse(Locality m) {
    Long dioceseId = m.getDiocese() != null ? m.getDiocese().getId() : null;
    return new LocalityResponse(m.getId(), m.getName(), m.getPostalCode(), m.getProvince(), dioceseId);
  }

  private ParishResponse toParishResponse(Parish p) {
    Long localityId = p.getLocality() != null ? p.getLocality().getId() : null;
    return new ParishResponse(p.getId(), p.getName(), p.getAddress(), p.getPhone(),
        p.getEmail(), p.getFoundedDate(), localityId);
  }

  private ParishionerResponse toParishionerResponse(Parishioner p) {
    Long parishId = p.getParish() != null ? p.getParish().getId() : null;
    Long familyId = p.getFamily() != null ? p.getFamily().getId() : null;
    return new ParishionerResponse(p.getId(), p.getFirstName(), p.getLastName(), p.getPesel(),
        p.getBirthDate(), p.getPhone(), p.getEmail(), parishId, familyId);
  }

  private RecordResponse toRecordResponse(ParishRecord k) {
    Long parishionerId = k.getParishioner() != null ? k.getParishioner().getId() : null;
    return new RecordResponse(k.getId(), k.getCreatedDate(), k.getDescription(), parishionerId);
  }

  private DocumentResponse toDocumentResponse(Document d) {
    Long recordId = d.getParishRecord() != null ? d.getParishRecord().getId() : null;
    return new DocumentResponse(d.getId(), d.getType(), d.getIssueDate(), d.getDescription(), recordId);
  }

  private ParishionerAggregateResponse toAggregateResponse(ParishionerAggregate a) {
    RecordResponse rec = a.getRecord() != null ? toRecordResponse(a.getRecord()) : null;
    return new ParishionerAggregateResponse(
        toParishionerResponse(a.getRoot()),
        a.hasRecord(),
        a.isProfileComplete(),
        a.documentCount(),
        rec,
        a.getDocuments().stream().map(this::toDocumentResponse).toList());
  }

  // ── Request / Response records ───────────────────────────────────────────────

  public record AddDioceseRequest(String name, String see, String bishop) {}
  public record PatchDioceseRequest(String name, String see, String bishop) {}
  public record DioceseResponse(Long id, String name, String see, String bishop) {}

  public record AddLocalityRequest(String name, String postalCode, String province, Long dioceseId) {}
  public record PatchLocalityRequest(String name, String postalCode, String province, Long dioceseId) {}
  public record LocalityResponse(Long id, String name, String postalCode, String province, Long dioceseId) {}

  public record AddParishRequest(String name, String address, String phone, String email,
      LocalDate erectionDate, @JsonAlias("localitiesId") Long localityId) {}
  public record PatchParishRequest(String name, String address, String phone, String email,
      LocalDate erectionDate, @JsonAlias("localitiesId") Long localityId) {}
  public record ParishResponse(Long id, String name, String address, String phone, String email,
      LocalDate erectionDate, Long localityId) {}

  public record AddParishionerRequest(String firstName, String lastName, String pesel,
      LocalDate birthDate, String phone, String email, Long parishId, Long familyId) {}
  public record PatchParishionerRequest(String firstName, String lastName, String pesel,
      LocalDate birthDate, String phone, String email, Long parishId, Long familyId) {}
  public record ParishionerResponse(Long id, String firstName, String lastName, String pesel,
      LocalDate birthDate, String phone, String email, Long parishId, Long familyId) {}

  public record ParishionerAggregateResponse(ParishionerResponse parishioner, boolean hasRecord,
      boolean profileComplete, int documentCount, RecordResponse record, List<DocumentResponse> documents) {}

  public record AddRecordRequest(LocalDate createdAt, String description, Long parishionerId) {}
  public record UpdateRecordRequest(String description) {}
  public record PatchRecordRequest(String description) {}
  public record RecordResponse(Long id, LocalDate createdAt, String description, Long parishionerId) {}

  public record AddDocumentRequest(String type, LocalDate issueDate, String description, Long recordId) {}
  public record UpdateDocumentRequest(String type, LocalDate issueDate, String description) {}
  public record PatchDocumentRequest(String type, LocalDate issueDate, String description) {}
  public record DocumentResponse(Long id, String type, LocalDate issueDate, String description, Long recordId) {}
}
