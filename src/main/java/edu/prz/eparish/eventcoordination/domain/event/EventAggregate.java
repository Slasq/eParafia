package edu.prz.eparish.eventcoordination.domain.event;

import edu.prz.eparish.eventcoordination.domain.intention.Intention;
import edu.prz.eparish.eventcoordination.domain.announcement.Announcement;
import edu.prz.eparish.eventcoordination.domain.offering.Offering;
import edu.prz.eparish.eventcoordination.domain.organizer.Organizer;
import edu.prz.eparish.eventcoordination.domain.participant.Participant;
import java.math.BigDecimal;
import java.util.List;

/**
 * Complex aggregate root — event coordination bounded context.
 *
 * Root: ParishEvent
 * Members: Intention, Announcement, Offering, Participant, Organizer
 *
 * Use cases covered:
 *  - UC: Zarządzanie wydarzeniami parafialnymi
 *  - UC: Przypisanie intencji
 *  - UC: Zarządzanie ogłoszeniami parafialnymi
 *  - UC: Prowadzenie harmonogramu
 *  - UC: Prowadzenie ewidencji ofiar
 *  - UC: Przypisanie uczestników i organizatorów
 */
public final class EventAggregate {

  private final ParishEvent root;
  private final List<Intention> intentions;
  private final List<Announcement> announcements;
  private final List<Offering> offerings;
  private final List<Participant> participants;
  private final List<Organizer> organizers;

  public EventAggregate(
      ParishEvent root,
      List<Intention> intentions,
      List<Announcement> announcements,
      List<Offering> offerings,
      List<Participant> participants,
      List<Organizer> organizers) {
    this.root = root;
    this.intentions = List.copyOf(intentions);
    this.announcements = List.copyOf(announcements);
    this.offerings = List.copyOf(offerings);
    this.participants = List.copyOf(participants);
    this.organizers = List.copyOf(organizers);
  }

  public ParishEvent getRoot() { return root; }
  public List<Intention> getIntentions() { return intentions; }
  public List<Announcement> getAnnouncements() { return announcements; }
  public List<Offering> getOfferings() { return offerings; }
  public List<Participant> getParticipants() { return participants; }
  public List<Organizer> getOrganizers() { return organizers; }

  // ── Domain logic — UC: Prowadzenie ewidencji ofiar ──────────────────────────

  public BigDecimal totalOfferings() {
    return offerings.stream()
        .map(Offering::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  // ── Domain logic — UC: Prowadzenie harmonogramu / przypisanie intencji ───────

  public long realizedIntentionCount() {
    return intentions.stream()
        .filter(i -> "REALIZED".equals(i.getStatus()))
        .count();
  }

  public long plannedIntentionCount() {
    return intentions.stream()
        .filter(i -> "PLANNED".equals(i.getStatus()))
        .count();
  }

  public boolean isIntentionAlreadyAssigned(String content) {
    return intentions.stream()
        .anyMatch(i -> i.getContent().equalsIgnoreCase(content));
  }

  // ── Domain logic — UC: Przypisanie uczestników i organizatorów ───────────────

  public int totalEngaged() {
    return participants.size() + organizers.size();
  }

  public boolean hasOrganizer() {
    return !organizers.isEmpty();
  }

  // ── Domain logic — UC: Zarządzanie ogłoszeniami ──────────────────────────────

  public int announcementCount() {
    return announcements.size();
  }
}
