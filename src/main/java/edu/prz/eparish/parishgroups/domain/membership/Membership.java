package edu.prz.eparish.parishgroups.domain.membership;

import edu.prz.eparish.pastoralcare.domain.parishioner.Parishioner;
import edu.prz.eparish.parishgroups.domain.group.ParishGroup;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Membership")
@Table(name = "membership")
@Data
public class Membership {

  @Id
  Long id;

  @Column(name = "start_date")
  LocalDate startDate;

  @Column(name = "end_date")
  LocalDate endDate;

  @ManyToOne
  @JoinColumn(name = "group_id")
  ParishGroup group;

  @ManyToOne
  @JoinColumn(name = "parishioner_id")
  Parishioner parishioner;
}
