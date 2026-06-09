package edu.prz.eparish.eventcoordination.domain.eventtype;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "EventType")
@Table(name = "event_type")
@Data
public class EventType {

  @Id
  Long id;

  String name;
}
