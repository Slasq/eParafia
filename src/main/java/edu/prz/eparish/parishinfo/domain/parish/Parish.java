package edu.prz.eparish.parishinfo.domain.parish;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Parish {

  @Id
  Long id;

  String name;
  String address;
  String phone;
  String email;
}
