package edu.prz.eparish.koordynacjawydarzen.domain.ogloszenie;

import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialne;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Ogloszenie")
@Table(name = "ogloszenie")
@Data
public class Ogloszenie {

  @Id
  Long id;

  String tresc;

  @ManyToOne
  @JoinColumn(name = "wydarzenie_id")
  WydarzenieParafialne wydarzenie;
}
