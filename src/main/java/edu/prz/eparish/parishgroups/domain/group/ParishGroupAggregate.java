package edu.prz.eparish.parishgroups.domain.group;

import edu.prz.eparish.parishgroups.domain.membership.Membership;
import java.time.LocalDate;
import java.util.List;

/**
 * Aggregate — parish groups bounded context.
 *
 * Root: ParishGroup
 * Members: Membership (1:N)
 *
 * Use cases covered:
 *  - UC: Dodaj grupę parafialną
 *  - UC: Zmień grupę parafialną
 *  - UC: Dodaj członkostwo (z opcjonalnymi datami rozpoczęcia i zakończenia)
 *  - UC: Przegląd aktywnych członków
 */
public final class ParishGroupAggregate {

  private final ParishGroup root;
  private final List<Membership> memberships;

  public ParishGroupAggregate(ParishGroup root, List<Membership> memberships) {
    this.root = root;
    this.memberships = List.copyOf(memberships);
  }

  public ParishGroup getRoot() { return root; }
  public List<Membership> getMemberships() { return memberships; }

  // ── Domain logic — UC: Dodaj członkostwo ────────────────────────────────────

  public int totalMemberCount() {
    return memberships.size();
  }

  // ── Domain logic — UC: Przegląd aktywnych członków ──────────────────────────

  public List<Membership> activeMembers() {
    return memberships.stream()
        .filter(c -> c.getEndDate() == null)
        .toList();
  }

  public List<Membership> activeMembersAt(LocalDate date) {
    return memberships.stream()
        .filter(c -> {
          boolean started = c.getStartDate() == null || !c.getStartDate().isAfter(date);
          boolean notEnded = c.getEndDate() == null || !c.getEndDate().isBefore(date);
          return started && notEnded;
        })
        .toList();
  }

  public boolean isMember(Long parishionerId) {
    return memberships.stream()
        .anyMatch(c -> parishionerId.equals(c.getParishioner().getId())
            && c.getEndDate() == null);
  }

  // ── Domain logic — historia ──────────────────────────────────────────────────

  public List<Membership> formerMembers() {
    return memberships.stream()
        .filter(c -> c.getEndDate() != null)
        .toList();
  }
}
