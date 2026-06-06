package edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie;

import edu.prz.eparish.koordynacjawydarzen.domain.intencja.Intencja;
import edu.prz.eparish.koordynacjawydarzen.domain.ogloszenie.Ogloszenie;
import edu.prz.eparish.koordynacjawydarzen.domain.ofiara.Ofiara;
import edu.prz.eparish.koordynacjawydarzen.domain.organizator.Organizator;
import edu.prz.eparish.koordynacjawydarzen.domain.uczestnik.Uczestnik;
import java.math.BigDecimal;
import java.util.List;

/**
 * Complex aggregate root — event coordination bounded context.
 *
 * Root: WydarzenieParafialne
 * Members: Intencja, Ogloszenie, Ofiara, Uczestnik, Organizator
 *
 * Use cases covered:
 *  - UC: Zarządzanie wydarzeniami parafialnymi
 *  - UC: Przypisanie intencji
 *  - UC: Zarządzanie ogłoszeniami parafialnymi
 *  - UC: Prowadzenie harmonogramu
 *  - UC: Prowadzenie ewidencji ofiar
 *  - UC: Przypisanie uczestników i organizatorów
 */
public final class WydarzenieAgregat {

  private final WydarzenieParafialne root;
  private final List<Intencja> intentions;
  private final List<Ogloszenie> announcements;
  private final List<Ofiara> offerings;
  private final List<Uczestnik> participants;
  private final List<Organizator> organizers;

  public WydarzenieAgregat(
      WydarzenieParafialne root,
      List<Intencja> intentions,
      List<Ogloszenie> announcements,
      List<Ofiara> offerings,
      List<Uczestnik> participants,
      List<Organizator> organizers) {
    this.root = root;
    this.intentions = List.copyOf(intentions);
    this.announcements = List.copyOf(announcements);
    this.offerings = List.copyOf(offerings);
    this.participants = List.copyOf(participants);
    this.organizers = List.copyOf(organizers);
  }

  public WydarzenieParafialne getRoot() { return root; }
  public List<Intencja> getIntencje() { return intentions; }
  public List<Ogloszenie> getOgloszenia() { return announcements; }
  public List<Ofiara> getOfiary() { return offerings; }
  public List<Uczestnik> getUczestnicy() { return participants; }
  public List<Organizator> getOrganizatorzy() { return organizers; }

  // ── Domain logic — UC: Prowadzenie ewidencji ofiar ──────────────────────────

  public BigDecimal totalOfferings() {
    return offerings.stream()
        .map(Ofiara::getKwota)
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
        .anyMatch(i -> i.getTresc().equalsIgnoreCase(content));
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
