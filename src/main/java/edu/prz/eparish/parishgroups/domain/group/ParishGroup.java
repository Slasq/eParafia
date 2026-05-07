package edu.prz.eparish.parishgroups.domain.group;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ParishGroup {

  @Id
  Long id;

  String name;
  String description;
  String guardian;
}
