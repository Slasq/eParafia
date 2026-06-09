package edu.prz.eparish.parishinformation.domain.parish;

import edu.prz.eparish.parishinformation.domain.locality.Locality;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Parish")
@Table(name = "parish")
@Data
public class Parish {

  @Id
  Long id;

  String name;
  String address;
  String phone;
  String email;

  @Column(name = "founded_date")
  LocalDate foundedDate;

  @ManyToOne
  @JoinColumn(name = "locality_id")
  Locality locality;
}
