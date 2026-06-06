package edu.prz.eparish.informacjeoparafii.domain.kartoteka;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KartotekaRepozytorium extends JpaRepository<Kartoteka, Long> {

  Optional<Kartoteka> findByParafianin_Id(Long parafianinId);
}
