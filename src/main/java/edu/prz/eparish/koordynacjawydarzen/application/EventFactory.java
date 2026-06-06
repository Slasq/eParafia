package edu.prz.eparish.koordynacjawydarzen.application;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.koordynacjawydarzen.domain.harmonogram.Harmonogram;
import edu.prz.eparish.koordynacjawydarzen.domain.harmonogram.HarmonogramRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.intencja.Intencja;
import edu.prz.eparish.koordynacjawydarzen.domain.intencja.IntencjaRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.ofiara.Ofiara;
import edu.prz.eparish.koordynacjawydarzen.domain.ofiara.OfiaraRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.ogloszenie.Ogloszenie;
import edu.prz.eparish.koordynacjawydarzen.domain.ogloszenie.OgloszenieRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.organizator.Organizator;
import edu.prz.eparish.koordynacjawydarzen.domain.organizator.OrganizatorRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.typwydarzenia.TypWydarzenia;
import edu.prz.eparish.koordynacjawydarzen.domain.typwydarzenia.TypWydarzeniaRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.uczestnik.Uczestnik;
import edu.prz.eparish.koordynacjawydarzen.domain.uczestnik.UczestnikRepozytorium;
import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialne;
import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialneRepozytorium;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventFactory {

  private final WydarzenieParafialneRepozytorium eventRepo;
  private final HarmonogramRepozytorium scheduleRepo;
  private final TypWydarzeniaRepozytorium eventTypeRepo;
  private final IntencjaRepozytorium intentionRepo;
  private final OfiaraRepozytorium offeringRepo;
  private final OgloszenieRepozytorium announcementRepo;
  private final UczestnikRepozytorium participantRepo;
  private final OrganizatorRepozytorium organizerRepo;

  public WydarzenieParafialne createEvent(
      String name, LocalDateTime dateTime, String place, String description,
      Parafia parish, TypWydarzenia eventType, Harmonogram schedule) {
    WydarzenieParafialne event = new WydarzenieParafialne();
    event.setId(EntityIds.nextId(eventRepo, WydarzenieParafialne::getId));
    event.setNazwa(name);
    event.setDataIGodzina(dateTime);
    event.setMiejsce(place);
    event.setOpis(description);
    event.setParafia(parish);
    event.setTypWydarzenia(eventType);
    event.setHarmonogram(schedule);
    return event;
  }

  public Harmonogram createSchedule(LocalDate date, LocalTime time, String description) {
    Harmonogram schedule = new Harmonogram();
    schedule.setId(EntityIds.nextId(scheduleRepo, Harmonogram::getId));
    schedule.setData(date);
    schedule.setGodzina(time);
    schedule.setOpis(description);
    return schedule;
  }

  public TypWydarzenia createEventType(String name) {
    TypWydarzenia eventType = new TypWydarzenia();
    eventType.setId(EntityIds.nextId(eventTypeRepo, TypWydarzenia::getId));
    eventType.setNazwa(name);
    return eventType;
  }

  public Intencja createIntention(
      String content, LocalDate date, String donor, WydarzenieParafialne event) {
    Intencja intention = new Intencja();
    intention.setId(EntityIds.nextId(intentionRepo, Intencja::getId));
    intention.setTresc(content);
    intention.setData(date);
    intention.setOfiarodawca(donor);
    intention.setStatus("PLANNED");
    intention.setWydarzenie(event);
    return intention;
  }

  public Ofiara createOffering(
      BigDecimal amount, LocalDate date, String type, WydarzenieParafialne event) {
    Ofiara offering = new Ofiara();
    offering.setId(EntityIds.nextId(offeringRepo, Ofiara::getId));
    offering.setKwota(amount);
    offering.setData(date);
    offering.setTyp(type);
    offering.setWydarzenie(event);
    return offering;
  }

  public Ogloszenie createAnnouncement(String content, WydarzenieParafialne event) {
    Ogloszenie announcement = new Ogloszenie();
    announcement.setId(EntityIds.nextId(announcementRepo, Ogloszenie::getId));
    announcement.setTresc(content);
    announcement.setWydarzenie(event);
    return announcement;
  }

  public Uczestnik createParticipant(
      String firstName, String lastName, String role, WydarzenieParafialne event) {
    Uczestnik participant = new Uczestnik();
    participant.setId(EntityIds.nextId(participantRepo, Uczestnik::getId));
    participant.setImie(firstName);
    participant.setNazwisko(lastName);
    participant.setRola(role);
    participant.setWydarzenie(event);
    return participant;
  }

  public Organizator createOrganizer(
      String firstName, String lastName, String role, WydarzenieParafialne event) {
    Organizator organizer = new Organizator();
    organizer.setId(EntityIds.nextId(organizerRepo, Organizator::getId));
    organizer.setImie(firstName);
    organizer.setNazwisko(lastName);
    organizer.setRola(role);
    organizer.setWydarzenie(event);
    return organizer;
  }
}
