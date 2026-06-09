package edu.prz.eparish.staffmanagement.domain.duty;

import edu.prz.eparish.staffmanagement.domain.position.Position;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Duty")
@Table(name = "duty")
@Data
public class Duty {

  @Id
  Long id;

  String name;
  String description;
  String status;

  @ManyToOne
  @JoinColumn(name = "position_id")
  Position position;
}
