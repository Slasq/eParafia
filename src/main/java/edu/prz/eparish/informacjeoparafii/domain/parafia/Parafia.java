package edu.prz.eparish.informacjeoparafii.domain.parafia;

import edu.prz.eparish.informacjeoparafii.domain.miejscowosc.Miejscowosc;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Parafia")
@Table(name = "parafia")
@Data
public class Parafia {

  @Id
  Long id;

  String nazwa;
  String adres;
  String telefon;
  String email;
  LocalDate dataErygowania;

  @OneToOne
  @JoinColumn(name = "miejscowosc_id")
  Miejscowosc miejscowosc;
}
