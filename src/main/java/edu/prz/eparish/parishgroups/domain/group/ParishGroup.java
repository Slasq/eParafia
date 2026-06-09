package edu.prz.eparish.parishgroups.domain.group;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "ParishGroup")
@Table(name = "parish_group")
@Data
public class ParishGroup {

  @Id
  Long id;

  String name;
  String description;
  String supervisor;
}
