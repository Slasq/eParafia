package edu.prz.eparish.informacjeoparafii.domain.kartoteka;

import edu.prz.eparish.duszpasterstwowiernych.domain.parafianin.Parafianin;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Kartoteka")
@Table(name = "kartoteka")
@Data
public class Kartoteka {

  @Id
  Long id;

  LocalDate dataUtworzenia;
  String opis;

  @OneToOne
  @JoinColumn(name = "parafianin_id")
  Parafianin parafianin;
}
