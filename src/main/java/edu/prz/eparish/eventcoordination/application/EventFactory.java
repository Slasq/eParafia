package edu.prz.eparish.eventcoordination.application;

import edu.prz.eparish.api.support.EntityIds;
import edu.prz.eparish.parishinformation.domain.parish.Parish;
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
import edu.prz.eparish.eventcoordination.domain.event.ParishEvent;
import edu.prz.eparish.eventcoordination.domain.event.ParishEventRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventFactory {

  private final ParishEventRepository eventRepo;
  private final ScheduleRepository scheduleRepo;
  private final EventTypeRepository eventTypeRepo;
  private final IntentionRepository intentionRepo;
  private final OfferingRepository offeringRepo;
  private final AnnouncementRepository announcementRepo;
  private final ParticipantRepository participantRepo;
  private final OrganizerRepository organizerRepo;

  public ParishEvent createEvent(
      String name, LocalDateTime dateTime, String place, String description,
      Parish parish, EventType eventType, Schedule schedule) {
    ParishEvent event = new ParishEvent();
    event.setId(EntityIds.nextId(eventRepo, ParishEvent::getId));
    event.setName(name);
    event.setDateTime(dateTime);
    event.setPlace(place);
    event.setDescription(description);
    event.setParish(parish);
    event.setEventType(eventType);
    event.setSchedule(schedule);
    return event;
  }

  public Schedule createSchedule(LocalDate date, LocalTime time, String description) {
    Schedule schedule = new Schedule();
    schedule.setId(EntityIds.nextId(scheduleRepo, Schedule::getId));
    schedule.setDate(date);
    schedule.setTime(time);
    schedule.setDescription(description);
    return schedule;
  }

  public EventType createEventType(String name) {
    EventType eventType = new EventType();
    eventType.setId(EntityIds.nextId(eventTypeRepo, EventType::getId));
    eventType.setName(name);
    return eventType;
  }

  public Intention createIntention(
      String content, LocalDate date, String donor, ParishEvent event) {
    Intention intention = new Intention();
    intention.setId(EntityIds.nextId(intentionRepo, Intention::getId));
    intention.setContent(content);
    intention.setDate(date);
    intention.setDonor(donor);
    intention.setStatus("PLANNED");
    intention.setEvent(event);
    return intention;
  }

  public Offering createOffering(
      BigDecimal amount, LocalDate date, String type, ParishEvent event) {
    Offering offering = new Offering();
    offering.setId(EntityIds.nextId(offeringRepo, Offering::getId));
    offering.setAmount(amount);
    offering.setDate(date);
    offering.setType(type);
    offering.setEvent(event);
    return offering;
  }

  public Announcement createAnnouncement(String content, ParishEvent event) {
    Announcement announcement = new Announcement();
    announcement.setId(EntityIds.nextId(announcementRepo, Announcement::getId));
    announcement.setContent(content);
    announcement.setEvent(event);
    return announcement;
  }

  public Participant createParticipant(
      String firstName, String lastName, String role, ParishEvent event) {
    Participant participant = new Participant();
    participant.setId(EntityIds.nextId(participantRepo, Participant::getId));
    participant.setFirstName(firstName);
    participant.setLastName(lastName);
    participant.setRole(role);
    participant.setEvent(event);
    return participant;
  }

  public Organizer createOrganizer(
      String firstName, String lastName, String role, ParishEvent event) {
    Organizer organizer = new Organizer();
    organizer.setId(EntityIds.nextId(organizerRepo, Organizer::getId));
    organizer.setFirstName(firstName);
    organizer.setLastName(lastName);
    organizer.setRole(role);
    organizer.setEvent(event);
    return organizer;
  }
}
