package edu.prz.eparish.informacjeoparafii.application;

import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.Parafianin;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinAgregat;
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
  private final DiecezjaRepozytorium dioceseRepo;
  private final MiejscowoscRepozytorium localityRepo;
  private final ParafiaRepozytorium parishRepo;
  private final ParafianinRepozytorium parishionerRepo;
  private final RodzinaRepozytorium familyRepo;
  private final KartotekaRepozytorium recordRepo;
  private final DokumentRepozytorium documentRepo;

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
      LocalDate erectionDate, Long localityId) {}

  public record PatchParishionerCommand(
      String firstName, String lastName, String pesel, LocalDate birthDate,
      String phone, String email, Long parishId, Long familyId) {}

  public record PatchRecordCommand(String description) {}

  public record PatchDocumentCommand(String type, LocalDate issueDate, String description) {}

  // ── UC: Dodaj diecezję (z opcjonalnym biskupem, siedzibą, zmianą nazwy) ─────

  public Diecezja addDiocese(AddDioceseCommand cmd) {
    Diecezja diocese = factory.createDiocese(cmd.name(), cmd.see(), cmd.bishop());
    return dioceseRepo.save(diocese);
  }

  public Diecezja updateDiocese(Long id, AddDioceseCommand cmd) {
    Diecezja diocese = requireDiocese(id);
    diocese.setNazwa(cmd.name());
    diocese.setSiedziba(cmd.see());
    diocese.setBiskup(cmd.bishop());
    return dioceseRepo.save(diocese);
  }

  public Diecezja patchDiocese(Long id, PatchDioceseCommand cmd) {
    Diecezja diocese = requireDiocese(id);
    if (cmd.name() != null) {
      diocese.setNazwa(cmd.name());
    }
    if (cmd.see() != null) {
      diocese.setSiedziba(cmd.see());
    }
    if (cmd.bishop() != null) {
      diocese.setBiskup(cmd.bishop());
    }
    return dioceseRepo.save(diocese);
  }

  public void deleteDiocese(Long id) {
    if (!dioceseRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Diocese not found");
    }
    dioceseRepo.deleteById(id);
  }

  public List<Diecezja> listDioceses(String name, String see, String bishop) {
    return ListFilterSupport.filter(dioceseRepo.findAll(),
        ListFilterSupport.containsIgnoreCase(name, Diecezja::getNazwa),
        ListFilterSupport.containsIgnoreCase(see, Diecezja::getSiedziba),
        ListFilterSupport.containsIgnoreCase(bishop, Diecezja::getBiskup));
  }

  // ── UC: Dodaj miejscowość ────────────────────────────────────────────────────

  public Miejscowosc addLocality(AddLocalityCommand cmd) {
    Diecezja diocese = requireDiocese(cmd.dioceseId());
    Miejscowosc locality = factory.createLocality(cmd.name(), cmd.postalCode(), cmd.province(), diocese);
    return localityRepo.save(locality);
  }

  public Miejscowosc updateLocality(Long id, AddLocalityCommand cmd) {
    Miejscowosc locality = localityRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locality not found"));
    locality.setNazwa(cmd.name());
    locality.setKodPocztowy(cmd.postalCode());
    locality.setWojewodztwo(cmd.province());
    locality.setDiecezja(requireDiocese(cmd.dioceseId()));
    return localityRepo.save(locality);
  }

  public Miejscowosc patchLocality(Long id, PatchLocalityCommand cmd) {
    Miejscowosc locality = localityRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locality not found"));
    if (cmd.name() != null) {
      locality.setNazwa(cmd.name());
    }
    if (cmd.postalCode() != null) {
      locality.setKodPocztowy(cmd.postalCode());
    }
    if (cmd.province() != null) {
      locality.setWojewodztwo(cmd.province());
    }
    if (cmd.dioceseId() != null) {
      locality.setDiecezja(requireDiocese(cmd.dioceseId()));
    }
    return localityRepo.save(locality);
  }

  public void deleteLocality(Long id) {
    if (!localityRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Locality not found");
    }
    localityRepo.deleteById(id);
  }

  public List<Miejscowosc> listLocalities(Long dioceseId, String province, String postalCode, String name) {
    return ListFilterSupport.filter(localityRepo.findAll(),
        ListFilterSupport.eqLong(dioceseId, m -> m.getDiecezja() != null ? m.getDiecezja().getId() : null),
        ListFilterSupport.eq(province, Miejscowosc::getWojewodztwo),
        ListFilterSupport.eq(postalCode, Miejscowosc::getKodPocztowy),
        ListFilterSupport.containsIgnoreCase(name, Miejscowosc::getNazwa));
  }

  // ── UC: Zarządzanie parafią ──────────────────────────────────────────────────

  public Parafia addParish(AddParishCommand cmd) {
    Miejscowosc locality = null;
    if (cmd.localityId() != null) {
      locality = localityRepo.findById(cmd.localityId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locality not found"));
    }
    Parafia parish = factory.createParish(
        cmd.name(), cmd.address(), cmd.phone(), cmd.email(), cmd.erectionDate(), locality);
    return parishRepo.save(parish);
  }

  public Parafia updateParish(Long id, AddParishCommand cmd) {
    Parafia parish = requireParish(id);
    parish.setNazwa(cmd.name());
    parish.setAdres(cmd.address());
    parish.setTelefon(cmd.phone());
    parish.setEmail(cmd.email());
    parish.setDataErygowania(cmd.erectionDate());
    if (cmd.localityId() != null) {
      Miejscowosc locality = localityRepo.findById(cmd.localityId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locality not found"));
      parish.setMiejscowosc(locality);
    }
    return parishRepo.save(parish);
  }

  public Parafia patchParish(Long id, PatchParishCommand cmd) {
    Parafia parish = requireParish(id);
    if (cmd.name() != null) {
      parish.setNazwa(cmd.name());
    }
    if (cmd.address() != null) {
      parish.setAdres(cmd.address());
    }
    if (cmd.phone() != null) {
      parish.setTelefon(cmd.phone());
    }
    if (cmd.email() != null) {
      parish.setEmail(cmd.email());
    }
    if (cmd.erectionDate() != null) {
      parish.setDataErygowania(cmd.erectionDate());
    }
    if (cmd.localityId() != null) {
      Miejscowosc locality = localityRepo.findById(cmd.localityId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locality not found"));
      parish.setMiejscowosc(locality);
    }
    return parishRepo.save(parish);
  }

  public void deleteParish(Long id) {
    if (!parishRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found");
    }
    parishRepo.deleteById(id);
  }

  public List<Parafia> listParishes(Long localityId, String name) {
    return ListFilterSupport.filter(parishRepo.findAll(),
        ListFilterSupport.eqLong(localityId, p -> p.getMiejscowosc() != null ? p.getMiejscowosc().getId() : null),
        ListFilterSupport.containsIgnoreCase(name, Parafia::getNazwa));
  }

  public Parafia getParish(Long id) {
    return requireParish(id);
  }

  // ── UC: Zarządzanie parafianami ──────────────────────────────────────────────

  public Parafianin registerParishioner(RegisterParishionerCommand cmd) {
    Parafia parish = requireParish(cmd.parishId());
    Parafianin parishioner = factory.createParishioner(
        cmd.firstName(), cmd.lastName(), cmd.pesel(), cmd.birthDate(),
        cmd.phone(), cmd.email(), parish);
    if (cmd.familyId() != null) {
      Rodzina family = familyRepo.findById(cmd.familyId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Family not found"));
      parishioner.setRodzina(family);
    }
    return parishionerRepo.save(parishioner);
  }

  public Parafianin updateParishioner(Long id, RegisterParishionerCommand cmd) {
    Parafianin parishioner = requireParishioner(id);
    parishioner.setImie(cmd.firstName());
    parishioner.setNazwisko(cmd.lastName());
    parishioner.setPesel(cmd.pesel());
    parishioner.setDataUrodzenia(cmd.birthDate());
    parishioner.setTelefon(cmd.phone());
    parishioner.setEmail(cmd.email());
    parishioner.setParafia(requireParish(cmd.parishId()));
    if (cmd.familyId() != null) {
      Rodzina family = familyRepo.findById(cmd.familyId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Family not found"));
      parishioner.setRodzina(family);
    }
    return parishionerRepo.save(parishioner);
  }

  public void removeParishioner(Long id) {
    if (!parishionerRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner not found");
    }
    parishionerRepo.deleteById(id);
  }

  public Parafianin patchParishioner(Long id, PatchParishionerCommand cmd) {
    Parafianin parishioner = requireParishioner(id);
    if (cmd.firstName() != null) {
      parishioner.setImie(cmd.firstName());
    }
    if (cmd.lastName() != null) {
      parishioner.setNazwisko(cmd.lastName());
    }
    if (cmd.pesel() != null) {
      parishioner.setPesel(cmd.pesel());
    }
    if (cmd.birthDate() != null) {
      parishioner.setDataUrodzenia(cmd.birthDate());
    }
    if (cmd.phone() != null) {
      parishioner.setTelefon(cmd.phone());
    }
    if (cmd.email() != null) {
      parishioner.setEmail(cmd.email());
    }
    if (cmd.parishId() != null) {
      parishioner.setParafia(requireParish(cmd.parishId()));
    }
    if (cmd.familyId() != null) {
      Rodzina family = familyRepo.findById(cmd.familyId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Family not found"));
      parishioner.setRodzina(family);
    }
    return parishionerRepo.save(parishioner);
  }

  public List<Parafianin> listParishioners(
      Long parishId, Long familyId, String pesel, String firstName, String lastName) {
    return ListFilterSupport.filter(parishionerRepo.findAll(),
        ListFilterSupport.eqLong(parishId, p -> p.getParafia() != null ? p.getParafia().getId() : null),
        ListFilterSupport.eqLong(familyId, p -> p.getRodzina() != null ? p.getRodzina().getId() : null),
        ListFilterSupport.eq(pesel, Parafianin::getPesel),
        ListFilterSupport.containsIgnoreCase(firstName, Parafianin::getImie),
        ListFilterSupport.containsIgnoreCase(lastName, Parafianin::getNazwisko));
  }

  public Parafianin getParishioner(Long id) {
    return requireParishioner(id);
  }

  // ── UC: Prowadzenie kartotek parafian ────────────────────────────────────────

  public Kartoteka createRecord(Long parishionerId, CreateRecordCommand cmd) {
    Parafianin parishioner = requireParishioner(parishionerId);
    Kartoteka record = factory.createRecord(cmd.createdAt(), cmd.description(), parishioner);
    return recordRepo.save(record);
  }

  public Kartoteka createRecordDirect(CreateRecordCommand cmd) {
    Parafianin parishioner = requireParishioner(cmd.parishionerId());
    Kartoteka record = factory.createRecord(cmd.createdAt(), cmd.description(), parishioner);
    return recordRepo.save(record);
  }

  // ── UC: Rejestracja zdarzeń religijnych ─────────────────────────────────────

  public Kartoteka updateRecord(Long id, String description) {
    Kartoteka record = recordRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    record.setOpis(description);
    return recordRepo.save(record);
  }

  public void deleteRecord(Long id) {
    if (!recordRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found");
    }
    recordRepo.deleteById(id);
  }

  public Kartoteka patchRecord(Long id, PatchRecordCommand cmd) {
    Kartoteka record = recordRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    if (cmd.description() != null) {
      record.setOpis(cmd.description());
    }
    return recordRepo.save(record);
  }

  public List<Kartoteka> listRecords(Long parishionerId) {
    if (parishionerId != null) {
      return recordRepo.findByParafianin_Id(parishionerId).map(List::of).orElse(List.of());
    }
    return recordRepo.findAll();
  }

  // ── UC: Zarządzanie dokumentacją ─────────────────────────────────────────────

  public Dokument addDocumentToParishioner(Long parishionerId, AddDocumentCommand cmd) {
    Kartoteka record = recordRepo.findByParafianin_Id(parishionerId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Parishioner has no record — create one first"));
    Dokument document = factory.createDocument(cmd.type(), cmd.issueDate(), cmd.description(), record);
    return documentRepo.save(document);
  }

  public Dokument addDocumentToRecord(Long recordId, AddDocumentCommand cmd) {
    Kartoteka record = recordRepo.findById(recordId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    Dokument document = factory.createDocument(cmd.type(), cmd.issueDate(), cmd.description(), record);
    return documentRepo.save(document);
  }

  public Dokument updateDocument(Long id, String type, LocalDate issueDate, String description) {
    Dokument document = documentRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
    document.setTyp(type);
    document.setDataWystawienia(issueDate);
    document.setOpis(description);
    return documentRepo.save(document);
  }

  public void deleteDocument(Long id) {
    if (!documentRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
    }
    documentRepo.deleteById(id);
  }

  public Dokument patchDocument(Long id, PatchDocumentCommand cmd) {
    Dokument document = documentRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));
    if (cmd.type() != null) {
      document.setTyp(cmd.type());
    }
    if (cmd.issueDate() != null) {
      document.setDataWystawienia(cmd.issueDate());
    }
    if (cmd.description() != null) {
      document.setOpis(cmd.description());
    }
    return documentRepo.save(document);
  }

  public List<Dokument> listDocuments(Long recordId, String type) {
    List<Dokument> source = recordId != null
        ? documentRepo.findByKartoteka_Id(recordId)
        : documentRepo.findAll();
    return ListFilterSupport.filter(source,
        ListFilterSupport.eq(type, Dokument::getTyp));
  }

  // ── Agregat ParafianinAgregat ────────────────────────────────────────────────

  public ParafianinAgregat getParishionerAggregate(Long id) {
    Parafianin parishioner = requireParishioner(id);
    Kartoteka record = recordRepo.findByParafianin_Id(id).orElse(null);
    List<Dokument> documents = record != null
        ? documentRepo.findByKartoteka_Id(record.getId())
        : List.of();
    return new ParafianinAgregat(parishioner, record, documents);
  }

  // ── Internal helpers ─────────────────────────────────────────────────────────

  private Diecezja requireDiocese(Long id) {
    return dioceseRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diocese not found"));
  }

  private Parafia requireParish(Long id) {
    return parishRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found"));
  }

  private Parafianin requireParishioner(Long id) {
    return parishionerRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner not found"));
  }
}
