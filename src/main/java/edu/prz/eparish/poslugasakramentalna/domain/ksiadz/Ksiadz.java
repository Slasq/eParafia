package edu.prz.eparish.poslugasakramentalna.domain.ksiadz;

import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Ksiadz")
@Table(name = "ksiadz")
@Data
public class Ksiadz {

  @Id
  Long id;

  String imie;
  String nazwisko;
  String telefon;
  String email;
  LocalDate dataSwiecen;
  String funkcja;

  @ManyToOne
  @JoinColumn(name = "parafia_id")
  Parafia parafia;
}
