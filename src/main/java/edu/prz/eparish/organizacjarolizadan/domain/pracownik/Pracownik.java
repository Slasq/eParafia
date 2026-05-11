package edu.prz.eparish.organizacjarolizadan.domain.pracownik;

import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import edu.prz.eparish.organizacjarolizadan.domain.stanowisko.Stanowisko;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Pracownik")
@Table(name = "pracownik")
@Data
public class Pracownik {

  @Id
  Long id;

  String imie;
  String nazwisko;

  @ManyToOne
  @JoinColumn(name = "stanowisko_id")
  Stanowisko stanowisko;

  @ManyToOne
  @JoinColumn(name = "parafia_id")
  Parafia parafia;
}
