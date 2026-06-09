package edu.prz.eparish.sacramentalministry.domain.priest;

import edu.prz.eparish.parishinformation.domain.parish.Parish;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Priest")
@Table(name = "priest")
@Data
public class Priest {

  @Id
  Long id;

  @Column(name = "first_name")
  String firstName;

  @Column(name = "last_name")
  String lastName;

  String phone;
  String email;

  @Column(name = "ordination_date")
  LocalDate ordinationDate;

  String role;

  @ManyToOne
  @JoinColumn(name = "parish_id")
  Parish parish;
}
