package edu.prz.eparish.sacramentalministry.domain.sacramentadministration;

import edu.prz.eparish.pastoralcare.domain.parishioner.ParishionerId;
import edu.prz.eparish.sacramentalministry.domain.priest.PriestId;
import edu.prz.eparish.sacramentalministry.domain.sacrament.SacramentId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "SacramentAdministration")
@Table(name = "sacrament_administration")
@Data
public class SacramentAdministration {

  @Id
  Long id;

  @Column(name = "administration_date")
  LocalDate administrationDate;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "parishioner_id"))
  ParishionerId parishionerId;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "priest_id"))
  PriestId priestId;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "sacrament_id"))
  SacramentId sacramentId;
}
