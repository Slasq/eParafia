package edu.prz.eparish.grupyparafialne.domain.czlonkostwo;

import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.Parafianin;
import edu.prz.eparish.grupyparafialne.domain.grupa.GrupaParafialna;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Czlonkostwo")
@Table(name = "czlonkostwo")
@Data
public class Czlonkostwo {

  @Id
  Long id;

  LocalDate dataOdKiedy;
  LocalDate dataDoKiedy;

  @ManyToOne
  @JoinColumn(name = "grupa_id")
  GrupaParafialna grupa;

  @ManyToOne
  @JoinColumn(name = "parafianin_id")
  Parafianin parafianin;
}
