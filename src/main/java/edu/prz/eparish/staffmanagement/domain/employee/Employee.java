package edu.prz.eparish.staffmanagement.domain.employee;

import edu.prz.eparish.parishinformation.domain.parish.Parish;
import edu.prz.eparish.staffmanagement.domain.position.Position;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Employee")
@Table(name = "employee")
@Data
public class Employee {

  @Id
  Long id;

  @Column(name = "first_name")
  String firstName;

  @Column(name = "last_name")
  String lastName;

  @ManyToOne
  @JoinColumn(name = "position_id")
  Position position;

  @ManyToOne
  @JoinColumn(name = "parish_id")
  Parish parish;
}
