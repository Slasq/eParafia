package edu.prz.eparish.poslugasakramentalna.domain.udzielaniesakramentu;

import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.ParafianinId;
import edu.prz.eparish.poslugasakramentalna.domain.ksiadz.KsiadzId;
import edu.prz.eparish.poslugasakramentalna.domain.sakrament.SakramentId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

  @Embedded
  @AttributeOverride(name = "wartosc", column = @Column(name = "parafianin_id"))
  ParafianinId parafianinId;

  @Embedded
  @AttributeOverride(name = "wartosc", column = @Column(name = "ksiadz_id"))
  KsiadzId ksiadzId;

  @Embedded
  @AttributeOverride(name = "wartosc", column = @Column(name = "sakrament_id"))
  SakramentId sakramentId;
}
