package edu.prz.eparish.informacjeoparafii.domain.miejscowosc;

import edu.prz.eparish.informacjeoparafii.domain.diecezja.Diecezja;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Miejscowosc")
@Table(name = "miejscowosc")
@Data
public class Miejscowosc {

  @Id
  Long id;

  String nazwa;
  String kodPocztowy;
  String wojewodztwo;

  @ManyToOne
  @JoinColumn(name = "diecezja_id")
  Diecezja diecezja;
}
