package edu.prz.eparish.poslugasakramentalna.domain.sakrament;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Sakrament")
@Table(name = "sakrament")
@Data
public class Sakrament {

  @Id
  Long id;

  String nazwa;
  String opis;
}
