package edu.prz.eparish.organizacjarolizadan.domain.obowiazek;

import edu.prz.eparish.organizacjarolizadan.domain.stanowisko.Stanowisko;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Obowiazek")
@Table(name = "obowiazek")
@Data
public class Obowiazek {

  @Id
  Long id;

  String nazwa;
  String opis;

  @ManyToOne
  @JoinColumn(name = "stanowisko_id")
  Stanowisko stanowisko;
}
