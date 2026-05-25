package edu.prz.eparish.koordynacjawydarzen.domain.wydarzenie;

import edu.prz.eparish.koordynacjawydarzen.domain.typwydarzenia.TypWydarzenia;
import edu.prz.eparish.koordynacjawydarzen.domain.harmonogram.Harmonogram;
import edu.prz.eparish.informacjeoparafii.domain.parafia.Parafia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Entity(name = "WydarzenieParafialne")
@Table(name = "wydarzenie_parafialne")
@Data
public class WydarzenieParafialne {

  @Id
  Long id;

  String nazwa;

  @Column(name = "data_i_godzina")
  LocalDateTime dataIGodzina;
  String miejsce;
  String opis;

  @ManyToOne
  @JoinColumn(name = "parafia_id")
  Parafia parafia;

  @ManyToOne
  @JoinColumn(name = "typ_wydarzenia_id")
  TypWydarzenia typWydarzenia;

  @ManyToOne
  @JoinColumn(name = "harmonogram_id")
  Harmonogram harmonogram;
}
