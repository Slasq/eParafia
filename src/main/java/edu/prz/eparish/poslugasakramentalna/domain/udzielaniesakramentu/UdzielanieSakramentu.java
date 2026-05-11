package edu.prz.eparish.poslugasakramentalna.domain.udzielaniesakramentu;

import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.Parafianin;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.Ksiadz;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.Sakrament;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "UdzielanieSakramentu")
@Table(name = "udzielanie_sakramentu")
@Data
public class UdzielanieSakramentu {

  @Id
  Long id;

  LocalDate dataUdzielenia;

  @ManyToOne
  @JoinColumn(name = "parafianin_id")
  Parafianin parafianin;

  @ManyToOne
  @JoinColumn(name = "ksiadz_id")
  Ksiadz ksiadz;

  @ManyToOne
  @JoinColumn(name = "sakrament_id")
  Sakrament sakrament;
}
