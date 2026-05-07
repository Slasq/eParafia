package edu.prz.eparish.eventcoordination.domain.event;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
public class ParishEvent {

  @Id
  Long id;

  String name;
  LocalDateTime eventDateTime;
  String place;
  String description;
}
