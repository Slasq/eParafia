package edu.prz.eparish.organizacjarolizadan.domain.stanowisko;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Stanowisko")
@Table(name = "stanowisko")
@Data
public class Stanowisko {

  @Id
  Long id;

  String nazwa;
  String opis;
}
