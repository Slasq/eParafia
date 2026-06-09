package edu.prz.eparish.pastoralcare.domain.parishioner;

import edu.prz.eparish.parishinformation.domain.document.Document;
import edu.prz.eparish.parishinformation.domain.record.ParishRecord;
import java.util.List;

/**
 * Aggregate — parishioner information bounded context.
 *
 * Root: Parishioner
 * Members: ParishRecord (1:1), Document (via record, 1:N)
 *
 * Use cases covered:
 *  - UC: Prowadzenie kartotek parafian
 *  - UC: Rejestracja zdarzeń religijnych
 *  - UC: Zarządzanie dokumentacją
 */
public final class ParishionerAggregate {

  private final Parishioner root;
  private final ParishRecord record;
  private final List<Document> documents;

  public ParishionerAggregate(Parishioner root, ParishRecord record, List<Document> documents) {
    this.root = root;
    this.record = record;
    this.documents = record != null ? List.copyOf(documents) : List.of();
  }

  public Parishioner getRoot() { return root; }
  public ParishRecord getRecord() { return record; }
  public List<Document> getDocuments() { return documents; }

  // ── Domain logic — UC: Prowadzenie kartotek parafian ────────────────────────

  public boolean hasRecord() {
    return record != null;
  }

  // ── Domain logic — UC: Zarządzanie dokumentacją ──────────────────────────────

  public int documentCount() {
    return documents.size();
  }

  public boolean hasDocumentOfType(String type) {
    return documents.stream()
        .anyMatch(d -> type.equalsIgnoreCase(d.getType()));
  }

  public List<Document> documentsOfType(String type) {
    return documents.stream()
        .filter(d -> type.equalsIgnoreCase(d.getType()))
        .toList();
  }

  // ── Domain logic — UC: Rejestracja zdarzeń religijnych ───────────────────────

  public boolean isProfileComplete() {
    Parishioner p = root;
    return p.getFirstName() != null && p.getLastName() != null
        && p.getPesel() != null && hasRecord();
  }
}
