package edu.prz.eparish.koordynacjawydarzen.domain.typwydarzenia;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "TypWydarzenia")
@Table(name = "typ_wydarzenia")
@Data
public class TypWydarzenia {

  @Id
  Long id;

  String nazwa;
}
