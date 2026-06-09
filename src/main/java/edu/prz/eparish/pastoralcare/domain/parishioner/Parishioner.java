package edu.prz.eparish.pastoralcare.domain.parishioner;

import edu.prz.eparish.pastoralcare.domain.family.Family;
import edu.prz.eparish.parishinformation.domain.parish.Parish;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Parishioner")
@Table(name = "parishioner")
@Data
public class Parishioner {

  @Id
  Long id;

  @Column(name = "first_name")
  String firstName;

  @Column(name = "last_name")
  String lastName;

  String pesel;

  @Column(name = "birth_date")
  LocalDate birthDate;
  String phone;
  String email;

  @ManyToOne
  @JoinColumn(name = "parish_id")
  Parish parish;

  @ManyToOne
  @JoinColumn(name = "family_id")
  Family family;
}
