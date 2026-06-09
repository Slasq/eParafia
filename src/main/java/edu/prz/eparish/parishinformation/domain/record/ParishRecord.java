package edu.prz.eparish.parishinformation.domain.record;

import edu.prz.eparish.pastoralcare.domain.parishioner.Parishioner;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "ParishRecord")
@Table(name = "parish_record")
@Data
public class ParishRecord {

  @Id
  Long id;

  @Column(name = "created_date")
  LocalDate createdDate;
  String description;

  @OneToOne
  @JoinColumn(name = "parishioner_id")
  Parishioner parishioner;
}
