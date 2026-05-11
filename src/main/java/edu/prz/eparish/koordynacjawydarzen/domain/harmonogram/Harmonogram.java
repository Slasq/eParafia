package edu.prz.eparish.koordynacjawydarzen.domain.harmonogram;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Entity(name = "Harmonogram")
@Table(name = "harmonogram")
@Data
public class Harmonogram {

  @Id
  Long id;

  LocalDate data;
  LocalTime godzina;
  String opis;
}
