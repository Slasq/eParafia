package edu.prz.eparish.parishinformation.domain.diocese;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Diocese")
@Table(name = "diocese")
@Data
public class Diocese {

  @Id
  Long id;

  String name;
  String see;
  String bishop;
}
