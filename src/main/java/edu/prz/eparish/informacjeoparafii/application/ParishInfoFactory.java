package edu.prz.eparish.informacjeoparafii.application;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.Parafianin;
import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinRepozytorium;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParishInfoFactory {

  private final DiecezjaRepozytorium dioceseRepo;
  private final MiejscowoscRepozytorium localityRepo;
  private final ParafiaRepozytorium parishRepo;
  private final ParafianinRepozytorium parishionerRepo;
  private final KartotekaRepozytorium recordRepo;
  private final DokumentRepozytorium documentRepo;

  public Diecezja createDiocese(String name, String see, String bishop) {
    Diecezja diocese = new Diecezja();
    diocese.setId(EntityIds.nextId(dioceseRepo, Diecezja::getId));
    diocese.setNazwa(name);
    diocese.setSiedziba(see);
    diocese.setBiskup(bishop);
    return diocese;
  }

  public Miejscowosc createLocality(
      String name, String postalCode, String province, Diecezja diocese) {
    Miejscowosc locality = new Miejscowosc();
    locality.setId(EntityIds.nextId(localityRepo, Miejscowosc::getId));
    locality.setNazwa(name);
    locality.setKodPocztowy(postalCode);
    locality.setWojewodztwo(province);
    locality.setDiecezja(diocese);
    return locality;
  }

  public Parafia createParish(
      String name, String address, String phone, String email,
      LocalDate erectionDate, Miejscowosc locality) {
    Parafia parish = new Parafia();
    parish.setId(EntityIds.nextId(parishRepo, Parafia::getId));
    parish.setNazwa(name);
    parish.setAdres(address);
    parish.setTelefon(phone);
    parish.setEmail(email);
    parish.setDataErygowania(erectionDate);
    parish.setMiejscowosc(locality);
    return parish;
  }

  public Parafianin createParishioner(
      String firstName, String lastName, String pesel, LocalDate birthDate,
      String phone, String email, Parafia parish) {
    Parafianin parishioner = new Parafianin();
    parishioner.setId(EntityIds.nextId(parishionerRepo, Parafianin::getId));
    parishioner.setImie(firstName);
    parishioner.setNazwisko(lastName);
    parishioner.setPesel(pesel);
    parishioner.setDataUrodzenia(birthDate);
    parishioner.setTelefon(phone);
    parishioner.setEmail(email);
    parishioner.setParafia(parish);
    return parishioner;
  }

  public Kartoteka createRecord(LocalDate createdAt, String description, Parafianin parishioner) {
    Kartoteka record = new Kartoteka();
    record.setId(EntityIds.nextId(recordRepo, Kartoteka::getId));
    record.setDataUtworzenia(createdAt);
    record.setOpis(description);
    record.setParafianin(parishioner);
    return record;
  }

  public Dokument createDocument(
      String type, LocalDate issueDate, String description, Kartoteka record) {
    Dokument document = new Dokument();
    document.setId(EntityIds.nextId(documentRepo, Dokument::getId));
    document.setTyp(type);
    document.setDataWystawienia(issueDate);
    document.setOpis(description);
    document.setKartoteka(record);
    return document;
  }
}
