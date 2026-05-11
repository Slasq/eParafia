package edu.prz.eparish.duszpasterstwowiernych.domain.parafianin;

import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.Rodzina;
import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Parafianin")
@Table(name = "parafianin")
@Data
public class Parafianin {

  @Id
  Long id;

  String imie;
  String nazwisko;
  String pesel;
  LocalDate dataUrodzenia;
  String telefon;
  String email;

  @ManyToOne
  @JoinColumn(name = "parafia_id")
  Parafia parafia;

  @ManyToOne
  @JoinColumn(name = "rodzina_id")
  Rodzina rodzina;
}
