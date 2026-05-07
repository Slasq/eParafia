package edu.prz.eparish.faithfulcare.domain.family;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Family {

  @Id
  Long id;

  String familyName;
  Integer membersCount;
  String city;
}
