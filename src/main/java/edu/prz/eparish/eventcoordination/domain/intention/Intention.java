package edu.prz.eparish.eventcoordination.domain.intention;

import edu.prz.eparish.eventcoordination.domain.event.ParishEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Intention")
@Table(name = "intention")
@Data
public class Intention {

  @Id
  Long id;

  String content;
  LocalDate date;
  String donor;
  String status;

  @ManyToOne
  @JoinColumn(name = "event_id")
  ParishEvent event;
}
