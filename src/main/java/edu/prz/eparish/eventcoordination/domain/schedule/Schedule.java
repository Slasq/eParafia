package edu.prz.eparish.eventcoordination.domain.schedule;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Entity(name = "Schedule")
@Table(name = "schedule")
@Data
public class Schedule {

  @Id
  Long id;

  LocalDate date;
  LocalTime time;
  String description;
}
