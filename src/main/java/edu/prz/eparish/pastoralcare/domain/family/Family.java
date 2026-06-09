package edu.prz.eparish.pastoralcare.domain.family;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Family")
@Table(name = "family")
@Data
public class Family {

  @Id
  Long id;

  @Column(name = "family_name")
  String familyName;

  @Column(name = "member_count")
  Integer memberCount;
}
