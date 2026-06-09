package edu.prz.eparish.staffmanagement.domain.position;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Position")
@Table(name = "position")
@Data
public class Position {

  @Id
  Long id;

  String name;
  String description;
}
