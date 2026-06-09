package edu.prz.eparish.eventcoordination.domain.participant;

import edu.prz.eparish.eventcoordination.domain.event.ParishEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Participant")
@Table(name = "participant")
@Data
public class Participant {

  @Id
  Long id;

  @Column(name = "first_name")
  String firstName;

  @Column(name = "last_name")
  String lastName;

  String role;

  @ManyToOne
  @JoinColumn(name = "event_id")
  ParishEvent event;
}
