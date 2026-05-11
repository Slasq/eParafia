package edu.prz.eparish.duszpasterstwowiernych.domain.rodzina;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Rodzina")
@Table(name = "rodzina")
@Data
public class Rodzina {

  @Id
  Long id;

  String nazwiskoRodziny;
  Integer liczbaCzlonkow;
}
