package edu.prz.eparish.api;

import edu.prz.eparish.eventcoordination.application.EventCoordinationService;
import edu.prz.eparish.eventcoordination.application.EventCoordinationService.AssignIntentionCommand;
import edu.prz.eparish.eventcoordination.application.EventCoordinationService.AssignPersonCommand;
import edu.prz.eparish.eventcoordination.application.EventCoordinationService.CreateEventCommand;
import edu.prz.eparish.eventcoordination.application.EventCoordinationService.CreateScheduleCommand;
import edu.prz.eparish.eventcoordination.application.EventCoordinationService.RecordOfferingCommand;
import edu.prz.eparish.eventcoordination.domain.schedule.Schedule;
import edu.prz.eparish.eventcoordination.domain.intention.Intention;
import edu.prz.eparish.eventcoordination.domain.offering.Offering;
import edu.prz.eparish.eventcoordination.domain.announcement.Announcement;
import edu.prz.eparish.eventcoordination.domain.organizer.Organizer;
import edu.prz.eparish.eventcoordination.domain.eventtype.EventType;
import edu.prz.eparish.eventcoordination.domain.participant.Participant;
import edu.prz.eparish.eventcoordination.domain.event.EventAggregate;
import edu.prz.eparish.eventcoordination.domain.event.ParishEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
@Tag(name = "Event Coordination", description = "Parish event management — EventAggregate (complex aggregate)")
public class EventCoordinationController {

  private final EventCoordinationService service;

  // ── EVENTS ──────────────────────────────────────────────────────────────────

  @GetMapping("/events")
  @Operation(summary = "List events (optional filters: parishId, eventTypeId, scheduleId, name)")
  public List<EventResponse> listEvents(
      @RequestParam(required = false) Long parishId,
      @RequestParam(required = false) Long eventTypeId,
      @RequestParam(required = false) Long scheduleId,
      @RequestParam(required = false) String name) {
    return service.listEvents(parishId, eventTypeId, scheduleId, name).stream()
        .map(this::toEventResponse).toList();
  }

  @GetMapping("/events/{id}")
  @Operation(summary = "Get event by ID")
  public EventResponse getEvent(@PathVariable Long id) {
    return toEventResponse(service.getEvent(id));
  }

  @PostMapping("/events")
  @Operation(summary = "UC: Zarządzanie wydarzeniami — create parish event")
  public ResponseEntity<EventResponse> addEvent(@RequestBody AddEventRequest req) {
    ParishEvent event = service.createEvent(new CreateEventCommand(
        req.name(), req.dateTime(), req.place(), req.description(),
        req.parishId(), req.eventTypeId(), req.scheduleId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toEventResponse(event));
  }

  @PutMapping("/events/{id}")
  @Operation(summary = "Update parish event")
  public EventResponse updateEvent(@PathVariable Long id, @RequestBody AddEventRequest req) {
    return toEventResponse(service.updateEvent(id, new CreateEventCommand(
        req.name(), req.dateTime(), req.place(), req.description(),
        req.parishId(), req.eventTypeId(), req.scheduleId())));
  }

  @PatchMapping("/events/{id}")
  @Operation(summary = "Partially update parish event")
  public EventResponse patchEvent(@PathVariable Long id, @RequestBody PatchEventRequest req) {
    return toEventResponse(service.patchEvent(id, new EventCoordinationService.PatchEventCommand(
        req.name(), req.dateTime(), req.place(), req.description(),
        req.parishId(), req.eventTypeId(), req.scheduleId())));
  }

  @DeleteMapping("/events/{id}")
  @Operation(summary = "Delete parish event")
  public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
    service.deleteEvent(id);
    return ResponseEntity.noContent().build();
  }

  // ── EVENT AGGREGATE ─────────────────────────────────────────────────────────

  @GetMapping("/events/{id}/aggregate")
  @Operation(summary = "UC: Full EventAggregate (complex aggregate)",
      description = "Returns event with all related entities: intentions, announcements, offerings, "
          + "participants, organizers. Includes domain statistics: total offerings, "
          + "realized intention count, total engaged persons.")
  public EventAggregateResponse getEventAggregate(@PathVariable Long id) {
    return toAggregateResponse(service.getEventAggregate(id));
  }

  // ── INTENTIONS — UC: Przypisanie intencji ───────────────────────────────────

  @GetMapping("/intentions")
  @Operation(summary = "List intentions (optional filters: eventId, status, date, donor)")
  public List<IntentionResponse> listIntentions(
      @RequestParam(required = false) Long eventId,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) LocalDate date,
      @RequestParam(required = false) String donor) {
    return service.listIntentions(eventId, status, date, donor).stream()
        .map(this::toIntentionResponse).toList();
  }

  @GetMapping("/intentions/{id}")
  @Operation(summary = "Get intention by ID")
  public IntentionResponse getIntention(@PathVariable Long id) {
    return toIntentionResponse(service.getIntention(id));
  }

  @PostMapping("/events/{id}/intentions")
  @Operation(summary = "UC: Przypisanie intencji — assign intention to event")
  public ResponseEntity<IntentionResponse> addIntentionToEvent(
      @PathVariable Long id, @RequestBody AddIntentionRequest req) {
    Intention intention = service.assignIntention(id,
        new AssignIntentionCommand(req.content(), req.date(), req.donor()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toIntentionResponse(intention));
  }

  @PatchMapping("/intentions/{id}")
  @Operation(summary = "Partially update intention")
  public IntentionResponse patchIntention(@PathVariable Long id, @RequestBody PatchIntentionRequest req) {
    return toIntentionResponse(service.patchIntention(id,
        new EventCoordinationService.PatchIntentionCommand(
            req.content(), req.date(), req.donor(), req.status())));
  }

  @DeleteMapping("/intentions/{id}")
  @Operation(summary = "Delete intention")
  public ResponseEntity<Void> deleteIntention(@PathVariable Long id) {
    service.deleteIntention(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/events/{eventId}/intentions/{intentionId}/realize")
  @Operation(summary = "UC: Prowadzenie harmonogramu — mark intention as realized")
  public IntentionResponse realizeIntention(
      @PathVariable Long eventId, @PathVariable Long intentionId) {
    return toIntentionResponse(service.realizeIntention(eventId, intentionId));
  }

  // ── ANNOUNCEMENTS — UC: Zarządzanie ogłoszeniami parafialnymi ───────────────

  @GetMapping("/announcements")
  @Operation(summary = "List announcements (optional filter: eventId)")
  public List<AnnouncementResponse> listAnnouncements(@RequestParam(required = false) Long eventId) {
    return service.listAnnouncements(eventId).stream().map(this::toAnnouncementResponse).toList();
  }

  @GetMapping("/announcements/{id}")
  @Operation(summary = "Get announcement by ID")
  public AnnouncementResponse getAnnouncement(@PathVariable Long id) {
    return toAnnouncementResponse(service.getAnnouncement(id));
  }

  @PostMapping("/events/{id}/announcements")
  @Operation(summary = "UC: Zarządzanie ogłoszeniami — add announcement to event")
  public ResponseEntity<AnnouncementResponse> addAnnouncementToEvent(
      @PathVariable Long id, @RequestBody AddAnnouncementRequest req) {
    Announcement announcement = service.addAnnouncement(id, req.content());
    return ResponseEntity.status(HttpStatus.CREATED).body(toAnnouncementResponse(announcement));
  }

  @PatchMapping("/announcements/{id}")
  @Operation(summary = "Partially update announcement")
  public AnnouncementResponse patchAnnouncement(
      @PathVariable Long id, @RequestBody PatchAnnouncementRequest req) {
    return toAnnouncementResponse(service.patchAnnouncement(id,
        new EventCoordinationService.PatchAnnouncementCommand(req.content())));
  }

  @DeleteMapping("/announcements/{id}")
  @Operation(summary = "Delete announcement")
  public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
    service.deleteAnnouncement(id);
    return ResponseEntity.noContent().build();
  }

  // ── OFFERINGS — UC: Prowadzenie ewidencji ofiar ─────────────────────────────

  @GetMapping("/offerings")
  @Operation(summary = "List offerings (optional filters: eventId, type, date)")
  public List<OfferingResponse> listOfferings(
      @RequestParam(required = false) Long eventId,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) LocalDate date) {
    return service.listOfferings(eventId, type, date).stream().map(this::toOfferingResponse).toList();
  }

  @GetMapping("/offerings/{id}")
  @Operation(summary = "Get offering by ID")
  public OfferingResponse getOffering(@PathVariable Long id) {
    return toOfferingResponse(service.getOffering(id));
  }

  @PostMapping("/events/{id}/offerings")
  @Operation(summary = "UC: Ewidencja ofiar — record offering for event")
  public ResponseEntity<OfferingResponse> addOfferingToEvent(
      @PathVariable Long id, @RequestBody AddOfferingRequest req) {
    Offering offering = service.recordOffering(id,
        new RecordOfferingCommand(req.amount(), req.date(), req.type()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toOfferingResponse(offering));
  }

  @PostMapping("/offerings")
  @Operation(summary = "UC: Ewidencja ofiar — record offering (eventId in body)")
  public ResponseEntity<OfferingResponse> addOfferingDirect(@RequestBody AddOfferingDirectRequest req) {
    Offering offering = service.recordOffering(req.eventId(),
        new RecordOfferingCommand(req.amount(), req.date(), req.type()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toOfferingResponse(offering));
  }

  @PatchMapping("/offerings/{id}")
  @Operation(summary = "Partially update offering")
  public OfferingResponse patchOffering(@PathVariable Long id, @RequestBody PatchOfferingRequest req) {
    return toOfferingResponse(service.patchOffering(id,
        new EventCoordinationService.PatchOfferingCommand(req.amount(), req.date(), req.type())));
  }

  @DeleteMapping("/offerings/{id}")
  @Operation(summary = "Delete offering")
  public ResponseEntity<Void> deleteOffering(@PathVariable Long id) {
    service.deleteOffering(id);
    return ResponseEntity.noContent().build();
  }

  // ── PARTICIPANTS — UC: Przypisanie uczestników i organizatorów ───────────────

  @GetMapping("/participants")
  @Operation(summary = "List participants (optional filters: eventId, role)")
  public List<ParticipantResponse> listParticipants(
      @RequestParam(required = false) Long eventId,
      @RequestParam(required = false) String role) {
    return service.listParticipants(eventId, role).stream().map(this::toParticipantResponse).toList();
  }

  @PostMapping("/events/{id}/participants")
  @Operation(summary = "UC: Przypisanie uczestników — assign participant to event")
  public ResponseEntity<ParticipantResponse> addParticipantToEvent(
      @PathVariable Long id, @RequestBody AddPersonRequest req) {
    Participant participant = service.assignParticipant(id,
        new AssignPersonCommand(req.firstName(), req.lastName(), req.role()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toParticipantResponse(participant));
  }

  @PostMapping("/participants")
  @Operation(summary = "UC: Przypisanie uczestników — assign participant (eventId in body)")
  public ResponseEntity<ParticipantResponse> addParticipantDirect(@RequestBody AddPersonDirectRequest req) {
    Participant participant = service.assignParticipant(req.eventId(),
        new AssignPersonCommand(req.firstName(), req.lastName(), req.role()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toParticipantResponse(participant));
  }

  @PatchMapping("/participants/{id}")
  @Operation(summary = "Partially update participant")
  public ParticipantResponse patchParticipant(@PathVariable Long id, @RequestBody PatchPersonRequest req) {
    return toParticipantResponse(service.patchParticipant(id,
        new EventCoordinationService.PatchPersonCommand(req.firstName(), req.lastName(), req.role())));
  }

  @DeleteMapping("/participants/{id}")
  @Operation(summary = "Remove participant")
  public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
    service.removeParticipant(id);
    return ResponseEntity.noContent().build();
  }

  // ── ORGANIZERS ───────────────────────────────────────────────────────────────

  @GetMapping("/organizers")
  @Operation(summary = "List organizers (optional filters: eventId, role)")
  public List<OrganizerResponse> listOrganizers(
      @RequestParam(required = false) Long eventId,
      @RequestParam(required = false) String role) {
    return service.listOrganizers(eventId, role).stream().map(this::toOrganizerResponse).toList();
  }

  @PostMapping("/events/{id}/organizers")
  @Operation(summary = "UC: Przypisanie organizatorów — assign organizer to event")
  public ResponseEntity<OrganizerResponse> addOrganizerToEvent(
      @PathVariable Long id, @RequestBody AddPersonRequest req) {
    Organizer organizer = service.assignOrganizer(id,
        new AssignPersonCommand(req.firstName(), req.lastName(), req.role()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toOrganizerResponse(organizer));
  }

  @PostMapping("/organizers")
  @Operation(summary = "UC: Przypisanie organizatorów — assign organizer (eventId in body)")
  public ResponseEntity<OrganizerResponse> addOrganizerDirect(@RequestBody AddPersonDirectRequest req) {
    Organizer organizer = service.assignOrganizer(req.eventId(),
        new AssignPersonCommand(req.firstName(), req.lastName(), req.role()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toOrganizerResponse(organizer));
  }

  @PatchMapping("/organizers/{id}")
  @Operation(summary = "Partially update organizer")
  public OrganizerResponse patchOrganizer(@PathVariable Long id, @RequestBody PatchPersonRequest req) {
    return toOrganizerResponse(service.patchOrganizer(id,
        new EventCoordinationService.PatchPersonCommand(req.firstName(), req.lastName(), req.role())));
  }

  @DeleteMapping("/organizers/{id}")
  @Operation(summary = "Remove organizer")
  public ResponseEntity<Void> deleteOrganizer(@PathVariable Long id) {
    service.removeOrganizer(id);
    return ResponseEntity.noContent().build();
  }

  // ── INTENTIONS direct (backwards-compat) ────────────────────────────────────

  @PostMapping("/intentions")
  @Operation(summary = "UC: Przypisanie intencji — create intention (eventId in body)")
  public ResponseEntity<IntentionResponse> addIntentionDirect(@RequestBody AddIntentionDirectRequest req) {
    Intention intention = service.assignIntention(req.eventId(),
        new AssignIntentionCommand(req.content(), req.date(), req.donor()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toIntentionResponse(intention));
  }

  @PostMapping("/announcements")
  @Operation(summary = "UC: Zarządzanie ogłoszeniami — create announcement (eventId in body)")
  public ResponseEntity<AnnouncementResponse> addAnnouncementDirect(@RequestBody AddAnnouncementDirectRequest req) {
    Announcement announcement = service.addAnnouncement(req.eventId(), req.content());
    return ResponseEntity.status(HttpStatus.CREATED).body(toAnnouncementResponse(announcement));
  }

  // ── EVENT TYPES ──────────────────────────────────────────────────────────────

  @GetMapping("/event-types")
  @Operation(summary = "List event types (optional filter: name)")
  public List<EventTypeResponse> listEventTypes(@RequestParam(required = false) String name) {
    return service.listEventTypes(name).stream()
        .map(t -> new EventTypeResponse(t.getId(), t.getName()))
        .toList();
  }

  @PostMapping("/event-types")
  @Operation(summary = "Create event type")
  public ResponseEntity<EventTypeResponse> addEventType(@RequestBody AddEventTypeRequest req) {
    EventType eventType = service.createEventType(req.name());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new EventTypeResponse(eventType.getId(), eventType.getName()));
  }

  @PutMapping("/event-types/{id}")
  @Operation(summary = "Update event type")
  public EventTypeResponse updateEventType(@PathVariable Long id, @RequestBody AddEventTypeRequest req) {
    EventType eventType = service.updateEventType(id, req.name());
    return new EventTypeResponse(eventType.getId(), eventType.getName());
  }

  @PatchMapping("/event-types/{id}")
  @Operation(summary = "Partially update event type")
  public EventTypeResponse patchEventType(@PathVariable Long id, @RequestBody PatchEventTypeRequest req) {
    EventType eventType = service.patchEventType(id, req.name());
    return new EventTypeResponse(eventType.getId(), eventType.getName());
  }

  @DeleteMapping("/event-types/{id}")
  @Operation(summary = "Delete event type")
  public ResponseEntity<Void> deleteEventType(@PathVariable Long id) {
    service.deleteEventType(id);
    return ResponseEntity.noContent().build();
  }

  // ── SCHEDULES — UC: Prowadzenie harmonogramu ─────────────────────────────────

  @GetMapping("/schedules")
  @Operation(summary = "List schedules (optional filter: date)")
  public List<ScheduleResponse> listSchedules(@RequestParam(required = false) LocalDate date) {
    return service.listSchedules(date).stream().map(this::toScheduleResponse).toList();
  }

  @GetMapping("/schedules/{id}")
  @Operation(summary = "Get schedule by ID")
  public ScheduleResponse getSchedule(@PathVariable Long id) {
    return toScheduleResponse(service.getSchedule(id));
  }

  @PostMapping("/schedules")
  @Operation(summary = "UC: Prowadzenie harmonogramu — create schedule")
  public ResponseEntity<ScheduleResponse> addSchedule(@RequestBody AddScheduleRequest req) {
    Schedule schedule = service.createSchedule(
        new CreateScheduleCommand(req.date(), req.time(), req.description()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toScheduleResponse(schedule));
  }

  @PutMapping("/schedules/{id}")
  @Operation(summary = "Update schedule")
  public ScheduleResponse updateSchedule(@PathVariable Long id, @RequestBody AddScheduleRequest req) {
    return toScheduleResponse(service.updateSchedule(id,
        new CreateScheduleCommand(req.date(), req.time(), req.description())));
  }

  @PatchMapping("/schedules/{id}")
  @Operation(summary = "Partially update schedule")
  public ScheduleResponse patchSchedule(@PathVariable Long id, @RequestBody PatchScheduleRequest req) {
    return toScheduleResponse(service.patchSchedule(id,
        new EventCoordinationService.PatchScheduleCommand(req.date(), req.time(), req.description())));
  }

  @DeleteMapping("/schedules/{id}")
  @Operation(summary = "Delete schedule")
  public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
    service.deleteSchedule(id);
    return ResponseEntity.noContent().build();
  }

  // ── Mapping helpers ──────────────────────────────────────────────────────────

  private EventResponse toEventResponse(ParishEvent w) {
    return new EventResponse(w.getId(), w.getName(), w.getDateTime(), w.getPlace(),
        w.getDescription(), w.getParish().getId(), w.getEventType().getId(), w.getSchedule().getId());
  }

  private EventAggregateResponse toAggregateResponse(EventAggregate a) {
    ParishEvent w = a.getRoot();
    return new EventAggregateResponse(
        w.getId(), w.getName(), w.getDateTime(), w.getPlace(), w.getDescription(),
        w.getParish().getId(), w.getEventType().getId(), w.getSchedule().getId(),
        a.totalOfferings(), a.realizedIntentionCount(), a.plannedIntentionCount(),
        a.totalEngaged(), a.announcementCount(),
        a.getIntentions().stream().map(this::toIntentionResponse).toList(),
        a.getAnnouncements().stream().map(this::toAnnouncementResponse).toList(),
        a.getOfferings().stream().map(this::toOfferingResponse).toList(),
        a.getParticipants().stream().map(this::toParticipantResponse).toList(),
        a.getOrganizers().stream().map(this::toOrganizerResponse).toList());
  }

  private IntentionResponse toIntentionResponse(Intention i) {
    return new IntentionResponse(i.getId(), i.getContent(), i.getDate(),
        i.getDonor(), i.getStatus(), i.getEvent().getId());
  }

  private AnnouncementResponse toAnnouncementResponse(Announcement o) {
    return new AnnouncementResponse(o.getId(), o.getContent(), o.getEvent().getId());
  }

  private OfferingResponse toOfferingResponse(Offering o) {
    return new OfferingResponse(o.getId(), o.getAmount(), o.getDate(), o.getType(), o.getEvent().getId());
  }

  private ParticipantResponse toParticipantResponse(Participant u) {
    return new ParticipantResponse(u.getId(), u.getFirstName(), u.getLastName(), u.getRole(), u.getEvent().getId());
  }

  private OrganizerResponse toOrganizerResponse(Organizer o) {
    return new OrganizerResponse(o.getId(), o.getFirstName(), o.getLastName(), o.getRole(), o.getEvent().getId());
  }

  private ScheduleResponse toScheduleResponse(Schedule h) {
    return new ScheduleResponse(h.getId(), h.getDate(), h.getTime(), h.getDescription());
  }

  // ── Request / Response records ───────────────────────────────────────────────

  public record AddEventRequest(String name, LocalDateTime dateTime, String place,
      String description, Long parishId, Long eventTypeId, Long scheduleId) {}

  public record PatchEventRequest(String name, LocalDateTime dateTime, String place,
      String description, Long parishId, Long eventTypeId, Long scheduleId) {}

  public record EventResponse(Long id, String name, LocalDateTime dateTime, String place,
      String description, Long parishId, Long eventTypeId, Long scheduleId) {}

  public record EventAggregateResponse(
      Long id, String name, LocalDateTime dateTime, String place, String description,
      Long parishId, Long eventTypeId, Long scheduleId,
      BigDecimal totalOfferings, long realizedIntentionCount, long plannedIntentionCount,
      int totalEngaged, int announcementCount,
      List<IntentionResponse> intentions,
      List<AnnouncementResponse> announcements,
      List<OfferingResponse> offerings,
      List<ParticipantResponse> participants,
      List<OrganizerResponse> organizers) {}

  public record AddIntentionRequest(String content, LocalDate date, String donor) {}

  public record PatchIntentionRequest(String content, LocalDate date, String donor, String status) {}

  public record IntentionResponse(Long id, String content, LocalDate date,
      String donor, String status, Long eventId) {}

  public record AddAnnouncementRequest(String content) {}

  public record PatchAnnouncementRequest(String content) {}

  public record AnnouncementResponse(Long id, String content, Long eventId) {}

  public record AddOfferingRequest(BigDecimal amount, LocalDate date, String type) {}

  public record AddOfferingDirectRequest(BigDecimal amount, LocalDate date, String type, Long eventId) {}

  public record PatchOfferingRequest(BigDecimal amount, LocalDate date, String type) {}

  public record OfferingResponse(Long id, BigDecimal amount, LocalDate date, String type, Long eventId) {}

  public record AddPersonRequest(String firstName, String lastName, String role) {}

  public record AddPersonDirectRequest(String firstName, String lastName, String role, Long eventId) {}

  public record PatchPersonRequest(String firstName, String lastName, String role) {}

  public record ParticipantResponse(Long id, String firstName, String lastName, String role, Long eventId) {}

  public record OrganizerResponse(Long id, String firstName, String lastName, String role, Long eventId) {}

  public record AddEventTypeRequest(String name) {}

  public record PatchEventTypeRequest(String name) {}

  public record EventTypeResponse(Long id, String name) {}

  public record AddScheduleRequest(LocalDate date, LocalTime time, String description) {}

  public record PatchScheduleRequest(LocalDate date, LocalTime time, String description) {}

  public record ScheduleResponse(Long id, LocalDate date, LocalTime time, String description) {}

  public record AddIntentionDirectRequest(String content, LocalDate date, String donor, Long eventId) {}

  public record AddAnnouncementDirectRequest(String content, Long eventId) {}
}
