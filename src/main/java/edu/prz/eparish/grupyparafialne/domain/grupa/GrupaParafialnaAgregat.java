package edu.prz.eparish.grupyparafialne.domain.grupa;

import edu.prz.eparish.grupyparafialne.domain.czlonkostwo.Czlonkostwo;
import java.time.LocalDate;
import java.util.List;

/**
 * Aggregate — parish groups bounded context.
 *
 * Root: GrupaParafialna
 * Members: Czlonkostwo (1:N)
 *
 * Use cases covered:
 *  - UC: Dodaj grupę parafialną
 *  - UC: Zmień grupę parafialną
 *  - UC: Dodaj członkostwo (z opcjonalnymi datami rozpoczęcia i zakończenia)
 *  - UC: Przegląd aktywnych członków
 */
public final class GrupaParafialnaAgregat {

  private final GrupaParafialna root;
  private final List<Czlonkostwo> memberships;

  public GrupaParafialnaAgregat(GrupaParafialna root, List<Czlonkostwo> memberships) {
    this.root = root;
    this.memberships = List.copyOf(memberships);
  }

  public GrupaParafialna getRoot() { return root; }
  public List<Czlonkostwo> getCzlonkostwa() { return memberships; }

  // ── Domain logic — UC: Dodaj członkostwo ────────────────────────────────────

  public int totalMemberCount() {
    return memberships.size();
  }

  // ── Domain logic — UC: Przegląd aktywnych członków ──────────────────────────

  public List<Czlonkostwo> activeMembers() {
    return memberships.stream()
        .filter(c -> c.getDataDoKiedy() == null)
        .toList();
  }

  public List<Czlonkostwo> activeMembersAt(LocalDate date) {
    return memberships.stream()
        .filter(c -> {
          boolean started = c.getDataOdKiedy() == null || !c.getDataOdKiedy().isAfter(date);
          boolean notEnded = c.getDataDoKiedy() == null || !c.getDataDoKiedy().isBefore(date);
          return started && notEnded;
        })
        .toList();
  }

  public boolean isMember(Long parishionerId) {
    return memberships.stream()
        .anyMatch(c -> parishionerId.equals(c.getParafianin().getId())
            && c.getDataDoKiedy() == null);
  }

  // ── Domain logic — historia ──────────────────────────────────────────────────

  public List<Czlonkostwo> formerMembers() {
    return memberships.stream()
        .filter(c -> c.getDataDoKiedy() != null)
        .toList();
  }

  // Legacy accessors
  public int liczbaCzlonkow() { return totalMemberCount(); }
  public List<Czlonkostwo> aktywniCzlonkowie() { return activeMembers(); }
}
