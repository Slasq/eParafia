package edu.prz.eparish.informacjeoparafii.domain.dokument;

import edu.prz.eparish.informacjeoparafii.domain.kartoteka.Kartoteka;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Dokument")
@Table(name = "dokument")
@Data
public class Dokument {

  @Id
  Long id;

  String typ;
  LocalDate dataWystawienia;
  String opis;

  @ManyToOne
  @JoinColumn(name = "kartoteka_id")
  Kartoteka kartoteka;
}
