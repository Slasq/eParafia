package edu.prz.eparish.roleorganization.domain.employee;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Employee {

  @Id
  Long id;

  String firstName;
  String lastName;
  String positionName;
}
