package edu.prz.eparish.grupyparafialne.domain.grupa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "GrupaParafialna")
@Table(name = "grupa_parafialna")
@Data
public class GrupaParafialna {

  @Id
  Long id;

  String nazwa;
  String opis;
  String opiekun;
}
