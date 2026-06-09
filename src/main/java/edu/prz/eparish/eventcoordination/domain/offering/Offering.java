package edu.prz.eparish.eventcoordination.domain.offering;

import edu.prz.eparish.eventcoordination.domain.event.ParishEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Offering")
@Table(name = "offering")
@Data
public class Offering {

  @Id
  Long id;

  BigDecimal amount;
  LocalDate date;
  String type;

  @ManyToOne
  @JoinColumn(name = "event_id")
  ParishEvent event;
}
