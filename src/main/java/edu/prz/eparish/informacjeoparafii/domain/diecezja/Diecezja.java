package edu.prz.eparish.informacjeoparafii.domain.diecezja;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "Diecezja")
@Table(name = "diecezja")
@Data
public class Diecezja {

  @Id
  Long id;

  String nazwa;
  String siedziba;
  String biskup;
}
