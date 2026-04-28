package edu.prz.eparish.sacramental.domain.sacrament;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Sacrament {

  @Id
  Long id;

  String name;
  String description;
}
