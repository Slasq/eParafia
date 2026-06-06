package edu.prz.eparish.duszpasterstwowiernych.domain.parafianin;

import edu.prz.eparish.informacjeoparafii.domain.dokument.Dokument;
import edu.prz.eparish.informacjeoparafii.domain.kartoteka.Kartoteka;
import java.util.List;

/**
 * Aggregate — parishioner information bounded context.
 *
 * Root: Parafianin
 * Members: Kartoteka (1:1), Dokument (via kartoteka, 1:N)
 *
 * Use cases covered:
 *  - UC: Prowadzenie kartotek parafian
 *  - UC: Rejestracja zdarzeń religijnych
 *  - UC: Zarządzanie dokumentacją
 */
public final class ParafianinAgregat {

  private final Parafianin root;
  private final Kartoteka record;
  private final List<Dokument> documents;

  public ParafianinAgregat(Parafianin root, Kartoteka record, List<Dokument> documents) {
    this.root = root;
    this.record = record;
    this.documents = record != null ? List.copyOf(documents) : List.of();
  }

  public Parafianin getRoot() { return root; }
  public Kartoteka getKartoteka() { return record; }
  public List<Dokument> getDokumenty() { return documents; }

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
        .anyMatch(d -> type.equalsIgnoreCase(d.getTyp()));
  }

  public List<Dokument> documentsOfType(String type) {
    return documents.stream()
        .filter(d -> type.equalsIgnoreCase(d.getTyp()))
        .toList();
  }

  // ── Domain logic — UC: Rejestracja zdarzeń religijnych ───────────────────────

  public boolean isProfileComplete() {
    Parafianin p = root;
    return p.getImie() != null && p.getNazwisko() != null
        && p.getPesel() != null && hasRecord();
  }

  // Legacy accessors kept for backwards compatibility
  public boolean maKartoteke() { return hasRecord(); }
  public int liczbaDokumentow() { return documentCount(); }
}
