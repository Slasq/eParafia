package edu.prz.eparish.pastoralcare.domain.familyaddress;

import edu.prz.eparish.pastoralcare.domain.family.Family;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "FamilyAddress")
@Table(name = "family_address")
@Data
public class FamilyAddress {

  @Id
  Long id;

  String street;

  @Column(name = "house_number")
  String houseNumber;

  @Column(name = "apartment_number")
  String apartmentNumber;

  @Column(name = "postal_code")
  String postalCode;

  String city;

  @OneToOne
  @JoinColumn(name = "family_id")
  Family family;
}
