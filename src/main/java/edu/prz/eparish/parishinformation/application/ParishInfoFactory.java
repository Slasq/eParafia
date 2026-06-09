package edu.prz.eparish.parishinformation.application;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.pastoralcare.domain.parishioner.Parishioner;
import edu.prz.eparish.pastoralcare.domain.parishioner.ParishionerRepository;
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
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParishInfoFactory {

  private final DioceseRepository dioceseRepo;
  private final LocalityRepository localityRepo;
  private final ParishRepository parishRepo;
  private final ParishionerRepository parishionerRepo;
  private final ParishRecordRepository recordRepo;
  private final DocumentRepository documentRepo;

  public Diocese createDiocese(String name, String see, String bishop) {
    Diocese diocese = new Diocese();
    diocese.setId(EntityIds.nextId(dioceseRepo, Diocese::getId));
    diocese.setName(name);
    diocese.setSee(see);
    diocese.setBishop(bishop);
    return diocese;
  }

  public Locality createLocality(
      String name, String postalCode, String province, Diocese diocese) {
    Locality locality = new Locality();
    locality.setId(EntityIds.nextId(localityRepo, Locality::getId));
    locality.setName(name);
    locality.setPostalCode(postalCode);
    locality.setProvince(province);
    locality.setDiocese(diocese);
    return locality;
  }

  public Parish createParish(
      String name, String address, String phone, String email,
      LocalDate erectionDate, Locality locality) {
    Parish parish = new Parish();
    parish.setId(EntityIds.nextId(parishRepo, Parish::getId));
    parish.setName(name);
    parish.setAddress(address);
    parish.setPhone(phone);
    parish.setEmail(email);
    parish.setFoundedDate(erectionDate);
    parish.setLocality(locality);
    return parish;
  }

  public Parishioner createParishioner(
      String firstName, String lastName, String pesel, LocalDate birthDate,
      String phone, String email, Parish parish) {
    Parishioner parishioner = new Parishioner();
    parishioner.setId(EntityIds.nextId(parishionerRepo, Parishioner::getId));
    parishioner.setFirstName(firstName);
    parishioner.setLastName(lastName);
    parishioner.setPesel(pesel);
    parishioner.setBirthDate(birthDate);
    parishioner.setPhone(phone);
    parishioner.setEmail(email);
    parishioner.setParish(parish);
    return parishioner;
  }

  public ParishRecord createRecord(LocalDate createdAt, String description, Parishioner parishioner) {
    ParishRecord record = new ParishRecord();
    record.setId(EntityIds.nextId(recordRepo, ParishRecord::getId));
    record.setCreatedDate(createdAt);
    record.setDescription(description);
    record.setParishioner(parishioner);
    return record;
  }

  public Document createDocument(
      String type, LocalDate issueDate, String description, ParishRecord record) {
    Document document = new Document();
    document.setId(EntityIds.nextId(documentRepo, Document::getId));
    document.setType(type);
    document.setIssueDate(issueDate);
    document.setDescription(description);
    document.setParishRecord(record);
    return document;
  }
}
