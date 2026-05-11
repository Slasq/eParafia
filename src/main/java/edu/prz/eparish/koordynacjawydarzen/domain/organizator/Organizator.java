package edu.prz.eparish.koordynacjawydarzen.domain.organizator;

import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialne;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Organizator")
@Table(name = "organizator")
@Data
public class Organizator {

  @Id
  Long id;

  String imie;
  String nazwisko;
  String rola;

  @ManyToOne
  @JoinColumn(name = "wydarzenie_id")
  WydarzenieParafialne wydarzenie;
}
