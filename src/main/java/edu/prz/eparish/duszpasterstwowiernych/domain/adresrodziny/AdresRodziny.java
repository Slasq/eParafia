package edu.prz.eparish.duszpasterstwowiernych.domain.adresrodziny;

import edu.prz.eparish.duszpasterstwowiernych.domain.rodzina.Rodzina;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "AdresRodziny")
@Table(name = "adres_rodziny")
@Data
public class AdresRodziny {

  @Id
  Long id;

  String ulica;
  String numerDomu;
  String numerMieszkania;
  String kodPocztowy;
  String miasto;

  @OneToOne
  @JoinColumn(name = "rodzina_id")
  Rodzina rodzina;
}
