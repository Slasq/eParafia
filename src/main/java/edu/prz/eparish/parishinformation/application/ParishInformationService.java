package edu.prz.eparish.parishinformation.application;

import edu.prz.eparish.pastoralcare.domain.parishioner.Parishioner;
import edu.prz.eparish.pastoralcare.domain.parishioner.ParishionerAggregate;
import edu.prz.eparish.pastoralcare.domain.parishioner.ParishionerRepository;
import edu.prz.eparish.pastoralcare.domain.family.Family;
import edu.prz.eparish.pastoralcare.domain.family.FamilyRepository;
import edu.prz.eparish.parishinformation.domain.diocese.Diocese;
import edu.prz.eparish.parishinformation.domain.diocese.DioceseRepository;
import edu.prz.eparish.parishinformation.domain.document.Document;
import edu.prz.eparish.parishinformation.domain.document.DocumentRepository;
import edu.prz.eparish.parishinformation.domain.record.ParishRecord;
import edu.prz.eparish.parishinformation.domain.record.ParishRecordRepository;
import edu.prz.eparish.parishinformation.domain.locality.Locality;
import edu.prz.eparish.parishinformation.domain.locality.LocalityRepository;
import edu.prz.eparish.parishinformation.domain.parish.Parish;
import edu.prz.eparish.parishinformation.domain.parish.ParishRepository;
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
public class ParishInformationService {

  private final ParishInfoFactory factory;
  private final DioceseRepository dioceseRepo;
  private final LocalityRepository localityRepo;
  private final ParishRepository parishRepo;
  private final ParishionerRepository parishionerRepo;
  private final FamilyRepository familyRepo;
  private final ParishRecordRepository recordRepo;
  private final DocumentRepository documentRepo;

  // ── Commands ────────────────────────────────────────────────────────────────

  public record AddDioceseCommand(String name, String see, String bishop) {}

  public record AddLocalityCommand(String name, String postalCode, String province, Long dioceseId) {}

  public record AddParishCommand(
      String name, String address, String phone, String email,
      LocalDate erectionDate, Long localityId) {}

  public record RegisterParishionerCommand(
      String firstName, String lastName, String pesel, LocalDate birthDate,
      String phone, String email, Long parishId, Long familyId) {}

  public record CreateRecordCommand(LocalDate createdAt, String description, Long parishionerId) {}

  public record AddDocumentCommand(String type, LocalDate issueDate, String description, Long recordId) {}

  public record PatchDioceseCommand(String name, String see, String bishop) {}

  public record PatchLocalityCommand(String name, String postalCode, String province, Long dioceseId) {}

  public record PatchParishCommand(
      String name, String address, String phone, String email,
      LocalDate erectionDate, boolean localityIdPresent, Long localityId) {}

  public record PatchParishionerCommand(
      String firstName, String lastName, String pesel, LocalDate birthDate,
      String phone, String email, Long parishId, boolean familyIdPresent, Long familyId) {}

  public record PatchRecordCommand(String description) {}

  public record PatchDocumentCommand(String type, LocalDate issueDate, String description) {}

  // ── UC: Dodaj diecezję (z opcjonalnym biskupem, siedzibą, zmianą nazwy) ─────

  public Diocese addDiocese(AddDioceseCommand cmd) {
    Diocese diocese = factory.createDiocese(cmd.name(), cmd.see(), cmd.bishop());
    return dioceseRepo.save(diocese);
  }

  public Diocese updateDiocese(Long id, AddDioceseCommand cmd) {
    Diocese diocese = requireDiocese(id);
    diocese.setName(cmd.name());
    diocese.setSee(cmd.see());
    diocese.setBishop(cmd.bishop());
    return dioceseRepo.save(diocese);
  }

  public Diocese patchDiocese(Long id, PatchDioceseCommand cmd) {
    Diocese diocese = requireDiocese(id);
    if (cmd.name() != null) {
      diocese.setName(cmd.name());
    }
    if (cmd.see() != null) {
      diocese.setSee(cmd.see());
    }
    if (cmd.bishop() != null) {
      diocese.setBishop(cmd.bishop());
    }
    return dioceseRepo.save(diocese);
  }

  public void deleteDiocese(Long id) {
    if (!dioceseRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Diocese not found");
    }
    dioceseRepo.deleteById(id);
  }

  public List<Diocese> listDioceses(String name, String see, String bishop) {
    return ListFilterSupport.filter(dioceseRepo.findAll(),
        ListFilterSupport.containsIgnoreCase(name, Diocese::getName),
        ListFilterSupport.containsIgnoreCase(see, Diocese::getSee),
        ListFilterSupport.containsIgnoreCase(bishop, Diocese::getBishop));
  }

  public Diocese getDiocese(Long id) {
    return requireDiocese(id);
  }

  // ── UC: Dodaj miejscowość ────────────────────────────────────────────────────

  public Locality addLocality(AddLocalityCommand cmd) {
    Diocese diocese = requireDiocese(cmd.dioceseId());
    Locality locality = factory.createLocality(cmd.name(), cmd.postalCode(), cmd.province(), diocese);
    return localityRepo.save(locality);
  }

  public Locality updateLocality(Long id, AddLocalityCommand cmd) {
    Locality locality = localityRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locality not found"));
    locality.setName(cmd.name());
    locality.setPostalCode(cmd.postalCode());
    locality.setProvince(cmd.province());
    locality.setDiocese(requireDiocese(cmd.dioceseId()));
    return localityRepo.save(locality);
  }

  public Locality patchLocality(Long id, PatchLocalityCommand cmd) {
    Locality locality = localityRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locality not found"));
    if (cmd.name() != null) {
      locality.setName(cmd.name());
    }
    if (cmd.postalCode() != null) {
      locality.setPostalCode(cmd.postalCode());
    }
    if (cmd.province() != null) {
      locality.setProvince(cmd.province());
    }
    if (cmd.dioceseId() != null) {
      locality.setDiocese(requireDiocese(cmd.dioceseId()));
    }
    return localityRepo.save(locality);
  }

  public void deleteLocality(Long id) {
    if (!localityRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Locality not found");
    }
    localityRepo.deleteById(id);
  }

  public List<Locality> listLocalities(Long dioceseId, String province, String postalCode, String name) {
    return ListFilterSupport.filter(localityRepo.findAll(),
        ListFilterSupport.eqLong(dioceseId, m -> m.getDiocese() != null ? m.getDiocese().getId() : null),
        ListFilterSupport.eq(province, Locality::getProvince),
        ListFilterSupport.eq(postalCode, Locality::getPostalCode),
        ListFilterSupport.containsIgnoreCase(name, Locality::getName));
  }

  // ── UC: Zarządzanie parafią ──────────────────────────────────────────────────

  public Parish addParish(AddParishCommand cmd) {
    Locality locality = null;
    if (cmd.localityId() != null) {
      locality = localityRepo.findById(cmd.localityId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locality not found"));
    }
    Parish parish = factory.createParish(
        cmd.name(), cmd.address(), cmd.phone(), cmd.email(), cmd.erectionDate(), locality);
    return parishRepo.save(parish);
  }

  public Parish updateParish(Long id, AddParishCommand cmd) {
    Parish parish = requireParish(id);
    parish.setName(cmd.name());
    parish.setAddress(cmd.address());
    parish.setPhone(cmd.phone());
    parish.setEmail(cmd.email());
    parish.setFoundedDate(cmd.erectionDate());
    if (cmd.localityId() != null) {
      Locality locality = localityRepo.findById(cmd.localityId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locality not found"));
      parish.setLocality(locality);
    }
    return parishRepo.save(parish);
  }

  public Parish patchParish(Long id, PatchParishCommand cmd) {
    Parish parish = requireParish(id);
    if (cmd.name() != null) {
      parish.setName(cmd.name());
    }
    if (cmd.address() != null) {
      parish.setAddress(cmd.address());
    }
    if (cmd.phone() != null) {
      parish.setPhone(cmd.phone());
    }
    if (cmd.email() != null) {
      parish.setEmail(cmd.email());
    }
    if (cmd.erectionDate() != null) {
      parish.setFoundedDate(cmd.erectionDate());
    }
    if (cmd.localityIdPresent()) {
      if (cmd.localityId() == null) {
        parish.setLocality(null);
      } else {
        Locality locality = localityRepo.findById(cmd.localityId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locality not found"));
        parish.setLocality(locality);
      }
    }
    return parishRepo.save(parish);
  }

  public void deleteParish(Long id) {
    if (!parishRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found");
    }
    parishRepo.deleteById(id);
  }

  public List<Parish> listParishes(Long localityId, String name) {
    return ListFilterSupport.filter(parishRepo.findAll(),
        ListFilterSupport.eqLong(localityId, p -> p.getLocality() != null ? p.getLocality().getId() : null),
        ListFilterSupport.containsIgnoreCase(name, Parish::getName));
  }

  public Parish getParish(Long id) {
    return requireParish(id);
  }

  // ── UC: Zarządzanie parafianami ──────────────────────────────────────────────

  public Parishioner registerParishioner(RegisterParishionerCommand cmd) {
    Parish parish = requireParish(cmd.parishId());
    Parishioner parishioner = factory.createParishioner(
        cmd.firstName(), cmd.lastName(), cmd.pesel(), cmd.birthDate(),
        cmd.phone(), cmd.email(), parish);
    if (cmd.familyId() != null) {
      Family family = familyRepo.findById(cmd.familyId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Family not found"));
      parishioner.setFamily(family);
    }
    return parishionerRepo.save(parishioner);
  }

  public Parishioner updateParishioner(Long id, RegisterParishionerCommand cmd) {
    Parishioner parishioner = requireParishioner(id);
    parishioner.setFirstName(cmd.firstName());
    parishioner.setLastName(cmd.lastName());
    parishioner.setPesel(cmd.pesel());
    parishioner.setBirthDate(cmd.birthDate());
    parishioner.setPhone(cmd.phone());
    parishioner.setEmail(cmd.email());
    parishioner.setParish(requireParish(cmd.parishId()));
    if (cmd.familyId() != null) {
      Family family = familyRepo.findById(cmd.familyId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Family not found"));
      parishioner.setFamily(family);
    }
    return parishionerRepo.save(parishioner);
  }

  public void removeParishioner(Long id) {
    if (!parishionerRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner not found");
    }
    parishionerRepo.deleteById(id);
  }

  public Parishioner patchParishioner(Long id, PatchParishionerCommand cmd) {
    Parishioner parishioner = requireParishioner(id);
    if (cmd.firstName() != null) {
      parishioner.setFirstName(cmd.firstName());
    }
    if (cmd.lastName() != null) {
      parishioner.setLastName(cmd.lastName());
    }
    if (cmd.pesel() != null) {
      parishioner.setPesel(cmd.pesel());
    }
    if (cmd.birthDate() != null) {
      parishioner.setBirthDate(cmd.birthDate());
    }
    if (cmd.phone() != null) {
      parishioner.setPhone(cmd.phone());
    }
    if (cmd.email() != null) {
      parishioner.setEmail(cmd.email());
    }
    if (cmd.parishId() != null) {
      parishioner.setParish(requireParish(cmd.parishId()));
    }
    if (cmd.familyIdPresent()) {
      if (cmd.familyId() == null) {
        parishioner.setFamily(null);
      } else {
        Family family = familyRepo.findById(cmd.familyId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Family not found"));
        parishioner.setFamily(family);
      }
    }
    return parishionerRepo.save(parishioner);
  }

  public List<Parishioner> listParishioners(
      Long parishId, Long familyId, String pesel, String firstName, String lastName) {
    return ListFilterSupport.filter(parishionerRepo.findAll(),
        ListFilterSupport.eqLong(parishId, p -> p.getParish() != null ? p.getParish().getId() : null),
        ListFilterSupport.eqLong(familyId, p -> p.getFamily() != null ? p.getFamily().getId() : null),
        ListFilterSupport.eq(pesel, Parishioner::getPesel),
        ListFilterSupport.containsIgnoreCase(firstName, Parishioner::getFirstName),
        ListFilterSupport.containsIgnoreCase(lastName, Parishioner::getLastName));
  }

  public Parishioner getParishioner(Long id) {
    return requireParishioner(id);
  }

  // ── UC: Prowadzenie kartotek parafian ────────────────────────────────────────

  public ParishRecord createRecord(Long parishionerId, CreateRecordCommand cmd) {
    Parishioner parishioner = requireParishioner(parishionerId);
    ParishRecord record = factory.createRecord(cmd.createdAt(), cmd.description(), parishioner);
    return recordRepo.save(record);
  }

  public ParishRecord createRecordDirect(CreateRecordCommand cmd) {
    Parishioner parishioner = requireParishioner(cmd.parishionerId());
    ParishRecord record = factory.createRecord(cmd.createdAt(), cmd.description(), parishioner);
    return recordRepo.save(record);
  }

  // ── UC: Rejestracja zdarzeń religijnych ─────────────────────────────────────

  public ParishRecord updateRecord(Long id, String description) {
    ParishRecord record = recordRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    record.setDescription(description);
    return recordRepo.save(record);
  }

  public void deleteRecord(Long id) {
    if (!recordRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found");
    }
    recordRepo.deleteById(id);
  }

  public ParishRecord patchRecord(Long id, PatchRecordCommand cmd) {
    ParishRecord record = recordRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    if (cmd.description() != null) {
      record.setDescription(cmd.description());
    }
    return recordRepo.save(record);
  }

  public List<ParishRecord> listRecords(Long parishionerId) {
    if (parishionerId != null) {
      return recordRepo.findByParishioner_Id(parishionerId).map(List::of).orElse(List.of());
    }
    return recordRepo.findAll();
  }

  // ── UC: Zarządzanie dokumentacją ─────────────────────────────────────────────

  public Document addDocumentToParishioner(Long parishionerId, AddDocumentCommand cmd) {
    ParishRecord record = recordRepo.findByParishioner_Id(parishionerId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Parishioner has no record — create one first"));
    Document document = factory.createDocument(cmd.type(), cmd.issueDate(), cmd.description(), record);
    return documentRepo.save(document);
  }

  public Document addDocumentToRecord(Long recordId, AddDocumentCommand cmd) {
    ParishRecord record = recordRepo.findById(recordId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    Document document = factory.createDocument(cmd.type(), cmd.issueDate(), cmd.description(), record);
    return documentRepo.save(document);
  }

  public Document updateDocument(Long id, String type, LocalDate issueDate, String description) {
    Document document = documentRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
    document.setType(type);
    document.setIssueDate(issueDate);
    document.setDescription(description);
    return documentRepo.save(document);
  }

  public void deleteDocument(Long id) {
    if (!documentRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
    }
    documentRepo.deleteById(id);
  }

  public Document patchDocument(Long id, PatchDocumentCommand cmd) {
    Document document = documentRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
    if (cmd.type() != null) {
      document.setType(cmd.type());
    }
    if (cmd.issueDate() != null) {
      document.setIssueDate(cmd.issueDate());
    }
    if (cmd.description() != null) {
      document.setDescription(cmd.description());
    }
    return documentRepo.save(document);
  }

  public List<Document> listDocuments(Long recordId, String type) {
    List<Document> source = recordId != null
        ? documentRepo.findByParishRecord_Id(recordId)
        : documentRepo.findAll();
    return ListFilterSupport.filter(source,
        ListFilterSupport.eq(type, Document::getType));
  }

  public Document getDocument(Long id) {
    return documentRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
  }

  // ── Agregat ParishionerAggregate ────────────────────────────────────────────────

  public ParishionerAggregate getParishionerAggregate(Long id) {
    Parishioner parishioner = requireParishioner(id);
    ParishRecord record = recordRepo.findByParishioner_Id(id).orElse(null);
    List<Document> documents = record != null
        ? documentRepo.findByParishRecord_Id(record.getId())
        : List.of();
    return new ParishionerAggregate(parishioner, record, documents);
  }

  // ── Internal helpers ─────────────────────────────────────────────────────────

  private Diocese requireDiocese(Long id) {
    return dioceseRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diocese not found"));
  }

  private Parish requireParish(Long id) {
    return parishRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found"));
  }

  private Parishioner requireParishioner(Long id) {
    return parishionerRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner not found"));
  }
}
