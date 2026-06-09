package edu.prz.eparish.sacramentalministry.domain.sacrament;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Sacrament")
@Table(name = "sacrament")
@Data
public class Sacrament {

  @Id
  Long id;

  String name;
  String description;
}
