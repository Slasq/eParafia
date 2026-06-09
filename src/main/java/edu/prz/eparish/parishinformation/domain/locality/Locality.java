package edu.prz.eparish.parishinformation.domain.locality;

import edu.prz.eparish.parishinformation.domain.diocese.Diocese;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Locality")
@Table(name = "locality")
@Data
public class Locality {

  @Id
  Long id;

  String name;

  @Column(name = "postal_code")
  String postalCode;

  String province;

  @ManyToOne
  @JoinColumn(name = "diocese_id")
  Diocese diocese;
}
