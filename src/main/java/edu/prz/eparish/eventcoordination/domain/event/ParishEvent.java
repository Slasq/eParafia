package edu.prz.eparish.eventcoordination.domain.event;

import edu.prz.eparish.eventcoordination.domain.eventtype.EventType;
import edu.prz.eparish.eventcoordination.domain.schedule.Schedule;
import edu.prz.eparish.parishinformation.domain.parish.Parish;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Entity(name = "ParishEvent")
@Table(name = "parish_event")
@Data
public class ParishEvent {

  @Id
  Long id;

  String name;

  @Column(name = "date_time")
  LocalDateTime dateTime;
  String place;
  String description;

  @ManyToOne
  @JoinColumn(name = "parish_id")
  Parish parish;

  @ManyToOne
  @JoinColumn(name = "event_type_id")
  EventType eventType;

  @ManyToOne
  @JoinColumn(name = "schedule_id")
  Schedule schedule;
}
