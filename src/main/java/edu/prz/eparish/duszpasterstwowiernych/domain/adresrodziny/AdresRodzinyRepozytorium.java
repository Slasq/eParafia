package edu.prz.eparish.duszpasterstwowiernych.domain.adresrodziny;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdresRodzinyRepozytorium extends JpaRepository<AdresRodziny, Long> {

  Optional<AdresRodziny> findByRodzina_Id(Long rodzinaId);
}
