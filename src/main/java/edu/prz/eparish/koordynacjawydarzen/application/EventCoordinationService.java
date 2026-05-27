package edu.prz.eparish.koordynacjawydarzen.application;

import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.informacjeoparafii.domain.parafia.ParafiaRepozytorium;
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
import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieAgregat;
import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialne;
import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialneRepozytorium;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class EventCoordinationService {

  private final EventFactory eventFactory;
  private final WydarzenieParafialneRepozytorium eventRepo;
  private final HarmonogramRepozytorium scheduleRepo;
  private final TypWydarzeniaRepozytorium eventTypeRepo;
  private final IntencjaRepozytorium intentionRepo;
  private final OgloszenieRepozytorium announcementRepo;
  private final OfiaraRepozytorium offeringRepo;
  private final UczestnikRepozytorium participantRepo;
  private final OrganizatorRepozytorium organizerRepo;
  private final ParafiaRepozytorium parishRepo;

  // ── Commands ────────────────────────────────────────────────────────────────

  public record CreateEventCommand(
      String name, LocalDateTime dateTime, String place, String description,
      Long parishId, Long eventTypeId, Long scheduleId) {}

  public record CreateScheduleCommand(LocalDate date, LocalTime time, String description) {}

  public record AssignIntentionCommand(String content, LocalDate date, String donor) {}

  public record RecordOfferingCommand(BigDecimal amount, LocalDate date, String type) {}

  public record AssignPersonCommand(String firstName, String lastName, String role) {}

  // ── UC: Zarządzanie wydarzeniami parafialnymi ────────────────────────────────

  public WydarzenieParafialne createEvent(CreateEventCommand cmd) {
    Parafia parish = requireParish(cmd.parishId());
    TypWydarzenia eventType = requireEventType(cmd.eventTypeId());
    Harmonogram schedule = requireSchedule(cmd.scheduleId());
    WydarzenieParafialne event = eventFactory.createEvent(
        cmd.name(), cmd.dateTime(), cmd.place(), cmd.description(),
        parish, eventType, schedule);
    return eventRepo.save(event);
  }

  public WydarzenieParafialne updateEvent(Long id, CreateEventCommand cmd) {
    WydarzenieParafialne event = requireEvent(id);
    event.setNazwa(cmd.name());
    event.setDataIGodzina(cmd.dateTime());
    event.setMiejsce(cmd.place());
    event.setOpis(cmd.description());
    event.setParafia(requireParish(cmd.parishId()));
    event.setTypWydarzenia(requireEventType(cmd.eventTypeId()));
    event.setHarmonogram(requireSchedule(cmd.scheduleId()));
    return eventRepo.save(event);
  }

  public void deleteEvent(Long id) {
    if (!eventRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
    }
    eventRepo.deleteById(id);
  }

  public List<WydarzenieParafialne> listEvents() {
    return eventRepo.findAll();
  }

  public WydarzenieParafialne getEvent(Long id) {
    return requireEvent(id);
  }

  // ── UC: Prowadzenie harmonogramu ─────────────────────────────────────────────

  public Harmonogram createSchedule(CreateScheduleCommand cmd) {
    Harmonogram schedule = eventFactory.createSchedule(cmd.date(), cmd.time(), cmd.description());
    return scheduleRepo.save(schedule);
  }

  public Harmonogram updateSchedule(Long id, CreateScheduleCommand cmd) {
    Harmonogram schedule = scheduleRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
    schedule.setData(cmd.date());
    schedule.setGodzina(cmd.time());
    schedule.setOpis(cmd.description());
    return scheduleRepo.save(schedule);
  }

  public List<Harmonogram> listSchedules() {
    return scheduleRepo.findAll();
  }

  // ── UC: Zarządzanie typami wydarzeń ─────────────────────────────────────────

  public TypWydarzenia createEventType(String name) {
    TypWydarzenia eventType = eventFactory.createEventType(name);
    return eventTypeRepo.save(eventType);
  }

  public TypWydarzenia updateEventType(Long id, String name) {
    TypWydarzenia eventType = requireEventType(id);
    eventType.setNazwa(name);
    return eventTypeRepo.save(eventType);
  }

  public List<TypWydarzenia> listEventTypes() {
    return eventTypeRepo.findAll();
  }

  // ── UC: Przypisanie intencji ─────────────────────────────────────────────────

  public Intencja assignIntention(Long eventId, AssignIntentionCommand cmd) {
    WydarzenieParafialne event = requireEvent(eventId);
    Intencja intention = eventFactory.createIntention(
        cmd.content(), cmd.date(), cmd.donor(), event);
    return intentionRepo.save(intention);
  }

  public Intencja realizeIntention(Long eventId, Long intentionId) {
    requireEvent(eventId);
    Intencja intention = intentionRepo.findById(intentionId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Intention not found"));
    intention.setStatus("REALIZED");
    return intentionRepo.save(intention);
  }

  public List<Intencja> listIntentions() {
    return intentionRepo.findAll();
  }

  public Intencja getIntention(Long id) {
    return intentionRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Intention not found"));
  }

  // ── UC: Zarządzanie ogłoszeniami parafialnymi ────────────────────────────────

  public Ogloszenie addAnnouncement(Long eventId, String content) {
    WydarzenieParafialne event = requireEvent(eventId);
    Ogloszenie announcement = eventFactory.createAnnouncement(content, event);
    return announcementRepo.save(announcement);
  }

  public List<Ogloszenie> listAnnouncements() {
    return announcementRepo.findAll();
  }

  public Ogloszenie getAnnouncement(Long id) {
    return announcementRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));
  }

  // ── UC: Prowadzenie ewidencji ofiar ─────────────────────────────────────────

  public Ofiara recordOffering(Long eventId, RecordOfferingCommand cmd) {
    WydarzenieParafialne event = requireEvent(eventId);
    Ofiara offering = eventFactory.createOffering(cmd.amount(), cmd.date(), cmd.type(), event);
    return offeringRepo.save(offering);
  }

  public List<Ofiara> listOfferings() {
    return offeringRepo.findAll();
  }

  // ── UC: Przypisanie uczestników i organizatorów ──────────────────────────────

  public Uczestnik assignParticipant(Long eventId, AssignPersonCommand cmd) {
    WydarzenieParafialne event = requireEvent(eventId);
    Uczestnik participant = eventFactory.createParticipant(
        cmd.firstName(), cmd.lastName(), cmd.role(), event);
    return participantRepo.save(participant);
  }

  public void removeParticipant(Long id) {
    if (!participantRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found");
    }
    participantRepo.deleteById(id);
  }

  public List<Uczestnik> listParticipants() {
    return participantRepo.findAll();
  }

  public Organizator assignOrganizer(Long eventId, AssignPersonCommand cmd) {
    WydarzenieParafialne event = requireEvent(eventId);
    Organizator organizer = eventFactory.createOrganizer(
        cmd.firstName(), cmd.lastName(), cmd.role(), event);
    return organizerRepo.save(organizer);
  }

  public void removeOrganizer(Long id) {
    if (!organizerRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organizer not found");
    }
    organizerRepo.deleteById(id);
  }

  public List<Organizator> listOrganizers() {
    return organizerRepo.findAll();
  }

  // ── Agregat WydarzenieAgregat ────────────────────────────────────────────────

  public WydarzenieAgregat getEventAggregate(Long id) {
    WydarzenieParafialne root = requireEvent(id);
    return new WydarzenieAgregat(
        root,
        intentionRepo.findByWydarzenie_Id(id),
        announcementRepo.findByWydarzenie_Id(id),
        offeringRepo.findByWydarzenie_Id(id),
        participantRepo.findByWydarzenie_Id(id),
        organizerRepo.findByWydarzenie_Id(id));
  }

  // ── Internal helpers ─────────────────────────────────────────────────────────

  private WydarzenieParafialne requireEvent(Long id) {
    return eventRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
  }

  private Parafia requireParish(Long id) {
    return parishRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found"));
  }

  private TypWydarzenia requireEventType(Long id) {
    return eventTypeRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event type not found"));
  }

  private Harmonogram requireSchedule(Long id) {
    return scheduleRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
  }
}
