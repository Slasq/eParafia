package edu.prz.eparish.eventcoordination.domain.announcement;

import edu.prz.eparish.eventcoordination.domain.event.ParishEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Announcement")
@Table(name = "announcement")
@Data
public class Announcement {

  @Id
  Long id;

  String content;

  @ManyToOne
  @JoinColumn(name = "event_id")
  ParishEvent event;
}
