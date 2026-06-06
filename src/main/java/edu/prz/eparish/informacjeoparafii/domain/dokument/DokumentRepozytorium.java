package edu.prz.eparish.informacjeoparafii.domain.dokument;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DokumentRepozytorium extends JpaRepository<Dokument, Long> {

  List<Dokument> findByKartoteka_Id(Long kartotekaId);
}
