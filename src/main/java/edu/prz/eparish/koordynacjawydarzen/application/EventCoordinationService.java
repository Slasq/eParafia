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
import edu.prz.eparish.api.support.ListFilterSupport;
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

  public record PatchIntentionCommand(String content, LocalDate date, String donor, String status) {}

  public record PatchAnnouncementCommand(String content) {}

  public record PatchOfferingCommand(BigDecimal amount, LocalDate date, String type) {}

  public record PatchPersonCommand(String firstName, String lastName, String role) {}

  public record PatchEventCommand(
      String name, LocalDateTime dateTime, String place, String description,
      Long parishId, Long eventTypeId, Long scheduleId) {}

  public record PatchScheduleCommand(LocalDate date, LocalTime time, String description) {}

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

  public List<WydarzenieParafialne> listEvents(Long parishId, Long eventTypeId, Long scheduleId, String name) {
    return ListFilterSupport.filter(eventRepo.findAll(),
        ListFilterSupport.eqLong(parishId, e -> e.getParafia() != null ? e.getParafia().getId() : null),
        ListFilterSupport.eqLong(eventTypeId, e -> e.getTypWydarzenia() != null ? e.getTypWydarzenia().getId() : null),
        ListFilterSupport.eqLong(scheduleId, e -> e.getHarmonogram() != null ? e.getHarmonogram().getId() : null),
        ListFilterSupport.containsIgnoreCase(name, WydarzenieParafialne::getNazwa));
  }

  public WydarzenieParafialne patchEvent(Long id, PatchEventCommand cmd) {
    WydarzenieParafialne event = requireEvent(id);
    if (cmd.name() != null) {
      event.setNazwa(cmd.name());
    }
    if (cmd.dateTime() != null) {
      event.setDataIGodzina(cmd.dateTime());
    }
    if (cmd.place() != null) {
      event.setMiejsce(cmd.place());
    }
    if (cmd.description() != null) {
      event.setOpis(cmd.description());
    }
    if (cmd.parishId() != null) {
      event.setParafia(requireParish(cmd.parishId()));
    }
    if (cmd.eventTypeId() != null) {
      event.setTypWydarzenia(requireEventType(cmd.eventTypeId()));
    }
    if (cmd.scheduleId() != null) {
      event.setHarmonogram(requireSchedule(cmd.scheduleId()));
    }
    return eventRepo.save(event);
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

  public Harmonogram patchSchedule(Long id, PatchScheduleCommand cmd) {
    Harmonogram schedule = scheduleRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
    if (cmd.date() != null) {
      schedule.setData(cmd.date());
    }
    if (cmd.time() != null) {
      schedule.setGodzina(cmd.time());
    }
    if (cmd.description() != null) {
      schedule.setOpis(cmd.description());
    }
    return scheduleRepo.save(schedule);
  }

  public void deleteSchedule(Long id) {
    if (!scheduleRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found");
    }
    scheduleRepo.deleteById(id);
  }

  public List<Harmonogram> listSchedules(LocalDate date) {
    return ListFilterSupport.filter(scheduleRepo.findAll(),
        ListFilterSupport.eq(date, Harmonogram::getData));
  }

  public Harmonogram getSchedule(Long id) {
    return scheduleRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
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

  public TypWydarzenia patchEventType(Long id, String name) {
    TypWydarzenia eventType = requireEventType(id);
    if (name != null) {
      eventType.setNazwa(name);
    }
    return eventTypeRepo.save(eventType);
  }

  public void deleteEventType(Long id) {
    if (!eventTypeRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event type not found");
    }
    eventTypeRepo.deleteById(id);
  }

  public List<TypWydarzenia> listEventTypes(String name) {
    return ListFilterSupport.filter(eventTypeRepo.findAll(),
        ListFilterSupport.containsIgnoreCase(name, TypWydarzenia::getNazwa));
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

  public Intencja patchIntention(Long id, PatchIntentionCommand cmd) {
    Intencja intention = intentionRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Intention not found"));
    if (cmd.content() != null) {
      intention.setTresc(cmd.content());
    }
    if (cmd.date() != null) {
      intention.setData(cmd.date());
    }
    if (cmd.donor() != null) {
      intention.setOfiarodawca(cmd.donor());
    }
    if (cmd.status() != null) {
      intention.setStatus(cmd.status());
    }
    return intentionRepo.save(intention);
  }

  public void deleteIntention(Long id) {
    if (!intentionRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Intention not found");
    }
    intentionRepo.deleteById(id);
  }

  public List<Intencja> listIntentions(Long eventId, String status, LocalDate date, String donor) {
    List<Intencja> source = eventId != null
        ? intentionRepo.findByWydarzenie_Id(eventId)
        : intentionRepo.findAll();
    return ListFilterSupport.filter(source,
        ListFilterSupport.eq(status, Intencja::getStatus),
        ListFilterSupport.eq(date, Intencja::getData),
        ListFilterSupport.containsIgnoreCase(donor, Intencja::getOfiarodawca));
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

  public Ogloszenie patchAnnouncement(Long id, PatchAnnouncementCommand cmd) {
    Ogloszenie announcement = announcementRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));
    if (cmd.content() != null) {
      announcement.setTresc(cmd.content());
    }
    return announcementRepo.save(announcement);
  }

  public void deleteAnnouncement(Long id) {
    if (!announcementRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
    }
    announcementRepo.deleteById(id);
  }

  public List<Ogloszenie> listAnnouncements(Long eventId) {
    if (eventId != null) {
      return announcementRepo.findByWydarzenie_Id(eventId);
    }
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

  public Ofiara getOffering(Long id) {
    return offeringRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offering not found"));
  }

  public Ofiara patchOffering(Long id, PatchOfferingCommand cmd) {
    Ofiara offering = getOffering(id);
    if (cmd.amount() != null) {
      offering.setKwota(cmd.amount());
    }
    if (cmd.date() != null) {
      offering.setData(cmd.date());
    }
    if (cmd.type() != null) {
      offering.setTyp(cmd.type());
    }
    return offeringRepo.save(offering);
  }

  public void deleteOffering(Long id) {
    if (!offeringRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Offering not found");
    }
    offeringRepo.deleteById(id);
  }

  public List<Ofiara> listOfferings(Long eventId, String type, LocalDate date) {
    List<Ofiara> source = eventId != null
        ? offeringRepo.findByWydarzenie_Id(eventId)
        : offeringRepo.findAll();
    return ListFilterSupport.filter(source,
        ListFilterSupport.eq(type, Ofiara::getTyp),
        ListFilterSupport.eq(date, Ofiara::getData));
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

  public Uczestnik patchParticipant(Long id, PatchPersonCommand cmd) {
    Uczestnik participant = participantRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
    if (cmd.firstName() != null) {
      participant.setImie(cmd.firstName());
    }
    if (cmd.lastName() != null) {
      participant.setNazwisko(cmd.lastName());
    }
    if (cmd.role() != null) {
      participant.setRola(cmd.role());
    }
    return participantRepo.save(participant);
  }

  public List<Uczestnik> listParticipants(Long eventId, String role) {
    List<Uczestnik> source = eventId != null
        ? participantRepo.findByWydarzenie_Id(eventId)
        : participantRepo.findAll();
    return ListFilterSupport.filter(source,
        ListFilterSupport.eq(role, Uczestnik::getRola));
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

  public Organizator patchOrganizer(Long id, PatchPersonCommand cmd) {
    Organizator organizer = organizerRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organizer not found"));
    if (cmd.firstName() != null) {
      organizer.setImie(cmd.firstName());
    }
    if (cmd.lastName() != null) {
      organizer.setNazwisko(cmd.lastName());
    }
    if (cmd.role() != null) {
      organizer.setRola(cmd.role());
    }
    return organizerRepo.save(organizer);
  }

  public List<Organizator> listOrganizers(Long eventId, String role) {
    List<Organizator> source = eventId != null
        ? organizerRepo.findByWydarzenie_Id(eventId)
        : organizerRepo.findAll();
    return ListFilterSupport.filter(source,
        ListFilterSupport.eq(role, Organizator::getRola));
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
