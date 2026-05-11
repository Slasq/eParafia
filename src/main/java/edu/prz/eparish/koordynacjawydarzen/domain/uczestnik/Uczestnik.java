package edu.prz.eparish.koordynacjawydarzen.domain.uczestnik;

import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialne;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Uczestnik")
@Table(name = "uczestnik")
@Data
public class Uczestnik {

  @Id
  Long id;

  String imie;
  String nazwisko;
  String rola;

  @ManyToOne
  @JoinColumn(name = "wydarzenie_id")
  WydarzenieParafialne wydarzenie;
}
