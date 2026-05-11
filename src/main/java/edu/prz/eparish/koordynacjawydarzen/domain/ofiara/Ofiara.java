package edu.prz.eparish.koordynacjawydarzen.domain.ofiara;

import edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie.WydarzenieParafialne;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Ofiara")
@Table(name = "ofiara")
@Data
public class Ofiara {

  @Id
  Long id;

  BigDecimal kwota;
  LocalDate data;
  String typ;

  @ManyToOne
  @JoinColumn(name = "wydarzenie_id")
  WydarzenieParafialne wydarzenie;
}
