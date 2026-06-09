package edu.prz.eparish.eventcoordination.application;

import edu.prz.eparish.parishinformation.domain.parish.Parish;
import edu.prz.eparish.parishinformation.domain.parish.ParishRepository;
import edu.prz.eparish.eventcoordination.domain.schedule.Schedule;
import edu.prz.eparish.eventcoordination.domain.schedule.ScheduleRepository;
import edu.prz.eparish.eventcoordination.domain.intention.Intention;
import edu.prz.eparish.eventcoordination.domain.intention.IntentionRepository;
import edu.prz.eparish.eventcoordination.domain.offering.Offering;
import edu.prz.eparish.eventcoordination.domain.offering.OfferingRepository;
import edu.prz.eparish.eventcoordination.domain.announcement.Announcement;
import edu.prz.eparish.eventcoordination.domain.announcement.AnnouncementRepository;
import edu.prz.eparish.eventcoordination.domain.organizer.Organizer;
import edu.prz.eparish.eventcoordination.domain.organizer.OrganizerRepository;
import edu.prz.eparish.eventcoordination.domain.eventtype.EventType;
import edu.prz.eparish.eventcoordination.domain.eventtype.EventTypeRepository;
import edu.prz.eparish.eventcoordination.domain.participant.Participant;
import edu.prz.eparish.eventcoordination.domain.participant.ParticipantRepository;
import edu.prz.eparish.eventcoordination.domain.event.EventAggregate;
import edu.prz.eparish.eventcoordination.domain.event.ParishEvent;
import edu.prz.eparish.eventcoordination.domain.event.ParishEventRepository;
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
  private final ParishEventRepository eventRepo;
  private final ScheduleRepository scheduleRepo;
  private final EventTypeRepository eventTypeRepo;
  private final IntentionRepository intentionRepo;
  private final AnnouncementRepository announcementRepo;
  private final OfferingRepository offeringRepo;
  private final ParticipantRepository participantRepo;
  private final OrganizerRepository organizerRepo;
  private final ParishRepository parishRepo;

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

  public ParishEvent createEvent(CreateEventCommand cmd) {
    Parish parish = requireParish(cmd.parishId());
    EventType eventType = requireEventType(cmd.eventTypeId());
    Schedule schedule = requireSchedule(cmd.scheduleId());
    ParishEvent event = eventFactory.createEvent(
        cmd.name(), cmd.dateTime(), cmd.place(), cmd.description(),
        parish, eventType, schedule);
    return eventRepo.save(event);
  }

  public ParishEvent updateEvent(Long id, CreateEventCommand cmd) {
    ParishEvent event = requireEvent(id);
    event.setName(cmd.name());
    event.setDateTime(cmd.dateTime());
    event.setPlace(cmd.place());
    event.setDescription(cmd.description());
    event.setParish(requireParish(cmd.parishId()));
    event.setEventType(requireEventType(cmd.eventTypeId()));
    event.setSchedule(requireSchedule(cmd.scheduleId()));
    return eventRepo.save(event);
  }

  public void deleteEvent(Long id) {
    if (!eventRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
    }
    eventRepo.deleteById(id);
  }

  public List<ParishEvent> listEvents(Long parishId, Long eventTypeId, Long scheduleId, String name) {
    return ListFilterSupport.filter(eventRepo.findAll(),
        ListFilterSupport.eqLong(parishId, e -> e.getParish() != null ? e.getParish().getId() : null),
        ListFilterSupport.eqLong(eventTypeId, e -> e.getEventType() != null ? e.getEventType().getId() : null),
        ListFilterSupport.eqLong(scheduleId, e -> e.getSchedule() != null ? e.getSchedule().getId() : null),
        ListFilterSupport.containsIgnoreCase(name, ParishEvent::getName));
  }

  public ParishEvent patchEvent(Long id, PatchEventCommand cmd) {
    ParishEvent event = requireEvent(id);
    if (cmd.name() != null) {
      event.setName(cmd.name());
    }
    if (cmd.dateTime() != null) {
      event.setDateTime(cmd.dateTime());
    }
    if (cmd.place() != null) {
      event.setPlace(cmd.place());
    }
    if (cmd.description() != null) {
      event.setDescription(cmd.description());
    }
    if (cmd.parishId() != null) {
      event.setParish(requireParish(cmd.parishId()));
    }
    if (cmd.eventTypeId() != null) {
      event.setEventType(requireEventType(cmd.eventTypeId()));
    }
    if (cmd.scheduleId() != null) {
      event.setSchedule(requireSchedule(cmd.scheduleId()));
    }
    return eventRepo.save(event);
  }

  public ParishEvent getEvent(Long id) {
    return requireEvent(id);
  }

  // ── UC: Prowadzenie harmonogramu ─────────────────────────────────────────────

  public Schedule createSchedule(CreateScheduleCommand cmd) {
    Schedule schedule = eventFactory.createSchedule(cmd.date(), cmd.time(), cmd.description());
    return scheduleRepo.save(schedule);
  }

  public Schedule updateSchedule(Long id, CreateScheduleCommand cmd) {
    Schedule schedule = scheduleRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
    schedule.setDate(cmd.date());
    schedule.setTime(cmd.time());
    schedule.setDescription(cmd.description());
    return scheduleRepo.save(schedule);
  }

  public Schedule patchSchedule(Long id, PatchScheduleCommand cmd) {
    Schedule schedule = scheduleRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
    if (cmd.date() != null) {
      schedule.setDate(cmd.date());
    }
    if (cmd.time() != null) {
      schedule.setTime(cmd.time());
    }
    if (cmd.description() != null) {
      schedule.setDescription(cmd.description());
    }
    return scheduleRepo.save(schedule);
  }

  public void deleteSchedule(Long id) {
    if (!scheduleRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found");
    }
    scheduleRepo.deleteById(id);
  }

  public List<Schedule> listSchedules(LocalDate date) {
    return ListFilterSupport.filter(scheduleRepo.findAll(),
        ListFilterSupport.eq(date, Schedule::getDate));
  }

  public Schedule getSchedule(Long id) {
    return scheduleRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
  }

  // ── UC: Zarządzanie typami wydarzeń ─────────────────────────────────────────

  public EventType createEventType(String name) {
    EventType eventType = eventFactory.createEventType(name);
    return eventTypeRepo.save(eventType);
  }

  public EventType updateEventType(Long id, String name) {
    EventType eventType = requireEventType(id);
    eventType.setName(name);
    return eventTypeRepo.save(eventType);
  }

  public EventType patchEventType(Long id, String name) {
    EventType eventType = requireEventType(id);
    if (name != null) {
      eventType.setName(name);
    }
    return eventTypeRepo.save(eventType);
  }

  public void deleteEventType(Long id) {
    if (!eventTypeRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event type not found");
    }
    eventTypeRepo.deleteById(id);
  }

  public List<EventType> listEventTypes(String name) {
    return ListFilterSupport.filter(eventTypeRepo.findAll(),
        ListFilterSupport.containsIgnoreCase(name, EventType::getName));
  }

  // ── UC: Przypisanie intencji ─────────────────────────────────────────────────

  public Intention assignIntention(Long eventId, AssignIntentionCommand cmd) {
    ParishEvent event = requireEvent(eventId);
    Intention intention = eventFactory.createIntention(
        cmd.content(), cmd.date(), cmd.donor(), event);
    return intentionRepo.save(intention);
  }

  public Intention realizeIntention(Long eventId, Long intentionId) {
    requireEvent(eventId);
    Intention intention = intentionRepo.findById(intentionId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Intention not found"));
    intention.setStatus("REALIZED");
    return intentionRepo.save(intention);
  }

  public Intention patchIntention(Long id, PatchIntentionCommand cmd) {
    Intention intention = intentionRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Intention not found"));
    if (cmd.content() != null) {
      intention.setContent(cmd.content());
    }
    if (cmd.date() != null) {
      intention.setDate(cmd.date());
    }
    if (cmd.donor() != null) {
      intention.setDonor(cmd.donor());
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

  public List<Intention> listIntentions(Long eventId, String status, LocalDate date, String donor) {
    List<Intention> source = eventId != null
        ? intentionRepo.findByEvent_Id(eventId)
        : intentionRepo.findAll();
    return ListFilterSupport.filter(source,
        ListFilterSupport.eq(status, Intention::getStatus),
        ListFilterSupport.eq(date, Intention::getDate),
        ListFilterSupport.containsIgnoreCase(donor, Intention::getDonor));
  }

  public Intention getIntention(Long id) {
    return intentionRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Intention not found"));
  }

  // ── UC: Zarządzanie ogłoszeniami parafialnymi ────────────────────────────────

  public Announcement addAnnouncement(Long eventId, String content) {
    ParishEvent event = requireEvent(eventId);
    Announcement announcement = eventFactory.createAnnouncement(content, event);
    return announcementRepo.save(announcement);
  }

  public Announcement patchAnnouncement(Long id, PatchAnnouncementCommand cmd) {
    Announcement announcement = announcementRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));
    if (cmd.content() != null) {
      announcement.setContent(cmd.content());
    }
    return announcementRepo.save(announcement);
  }

  public void deleteAnnouncement(Long id) {
    if (!announcementRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found");
    }
    announcementRepo.deleteById(id);
  }

  public List<Announcement> listAnnouncements(Long eventId) {
    if (eventId != null) {
      return announcementRepo.findByEvent_Id(eventId);
    }
    return announcementRepo.findAll();
  }

  public Announcement getAnnouncement(Long id) {
    return announcementRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));
  }

  // ── UC: Prowadzenie ewidencji ofiar ─────────────────────────────────────────

  public Offering recordOffering(Long eventId, RecordOfferingCommand cmd) {
    ParishEvent event = requireEvent(eventId);
    Offering offering = eventFactory.createOffering(cmd.amount(), cmd.date(), cmd.type(), event);
    return offeringRepo.save(offering);
  }

  public Offering getOffering(Long id) {
    return offeringRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offering not found"));
  }

  public Offering patchOffering(Long id, PatchOfferingCommand cmd) {
    Offering offering = getOffering(id);
    if (cmd.amount() != null) {
      offering.setAmount(cmd.amount());
    }
    if (cmd.date() != null) {
      offering.setDate(cmd.date());
    }
    if (cmd.type() != null) {
      offering.setType(cmd.type());
    }
    return offeringRepo.save(offering);
  }

  public void deleteOffering(Long id) {
    if (!offeringRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Offering not found");
    }
    offeringRepo.deleteById(id);
  }

  public List<Offering> listOfferings(Long eventId, String type, LocalDate date) {
    List<Offering> source = eventId != null
        ? offeringRepo.findByEvent_Id(eventId)
        : offeringRepo.findAll();
    return ListFilterSupport.filter(source,
        ListFilterSupport.eq(type, Offering::getType),
        ListFilterSupport.eq(date, Offering::getDate));
  }

  // ── UC: Przypisanie uczestników i organizatorów ──────────────────────────────

  public Participant assignParticipant(Long eventId, AssignPersonCommand cmd) {
    ParishEvent event = requireEvent(eventId);
    Participant participant = eventFactory.createParticipant(
        cmd.firstName(), cmd.lastName(), cmd.role(), event);
    return participantRepo.save(participant);
  }

  public void removeParticipant(Long id) {
    if (!participantRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found");
    }
    participantRepo.deleteById(id);
  }

  public Participant patchParticipant(Long id, PatchPersonCommand cmd) {
    Participant participant = participantRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participant not found"));
    if (cmd.firstName() != null) {
      participant.setFirstName(cmd.firstName());
    }
    if (cmd.lastName() != null) {
      participant.setLastName(cmd.lastName());
    }
    if (cmd.role() != null) {
      participant.setRole(cmd.role());
    }
    return participantRepo.save(participant);
  }

  public List<Participant> listParticipants(Long eventId, String role) {
    List<Participant> source = eventId != null
        ? participantRepo.findByEvent_Id(eventId)
        : participantRepo.findAll();
    return ListFilterSupport.filter(source,
        ListFilterSupport.eq(role, Participant::getRole));
  }

  public Organizer assignOrganizer(Long eventId, AssignPersonCommand cmd) {
    ParishEvent event = requireEvent(eventId);
    Organizer organizer = eventFactory.createOrganizer(
        cmd.firstName(), cmd.lastName(), cmd.role(), event);
    return organizerRepo.save(organizer);
  }

  public void removeOrganizer(Long id) {
    if (!organizerRepo.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organizer not found");
    }
    organizerRepo.deleteById(id);
  }

  public Organizer patchOrganizer(Long id, PatchPersonCommand cmd) {
    Organizer organizer = organizerRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organizer not found"));
    if (cmd.firstName() != null) {
      organizer.setFirstName(cmd.firstName());
    }
    if (cmd.lastName() != null) {
      organizer.setLastName(cmd.lastName());
    }
    if (cmd.role() != null) {
      organizer.setRole(cmd.role());
    }
    return organizerRepo.save(organizer);
  }

  public List<Organizer> listOrganizers(Long eventId, String role) {
    List<Organizer> source = eventId != null
        ? organizerRepo.findByEvent_Id(eventId)
        : organizerRepo.findAll();
    return ListFilterSupport.filter(source,
        ListFilterSupport.eq(role, Organizer::getRole));
  }

  // ── Agregat EventAggregate ────────────────────────────────────────────────────

  public EventAggregate getEventAggregate(Long id) {
    ParishEvent root = requireEvent(id);
    return new EventAggregate(
        root,
        intentionRepo.findByEvent_Id(id),
        announcementRepo.findByEvent_Id(id),
        offeringRepo.findByEvent_Id(id),
        participantRepo.findByEvent_Id(id),
        organizerRepo.findByEvent_Id(id));
  }

  // ── Internal helpers ─────────────────────────────────────────────────────────

  private ParishEvent requireEvent(Long id) {
    return eventRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
  }

  private Parish requireParish(Long id) {
    return parishRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish not found"));
  }

  private EventType requireEventType(Long id) {
    return eventTypeRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event type not found"));
  }

  private Schedule requireSchedule(Long id) {
    return scheduleRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
  }
}
