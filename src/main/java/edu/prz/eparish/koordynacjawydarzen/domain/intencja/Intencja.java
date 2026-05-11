package edu.prz.eparish.koordynacjawydarzen.domain.intencja;

import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialne;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Intencja")
@Table(name = "intencja")
@Data
public class Intencja {

  @Id
  Long id;

  String tresc;
  LocalDate data;
  String ofiarodawca;
  String status;

  @ManyToOne
  @JoinColumn(name = "wydarzenie_id")
  WydarzenieParafialne wydarzenie;
}
