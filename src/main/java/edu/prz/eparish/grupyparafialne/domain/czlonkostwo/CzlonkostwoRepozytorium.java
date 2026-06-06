package edu.prz.eparish.grupyparafialne.domain.czlonkostwo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CzlonkostwoRepozytorium extends JpaRepository<Czlonkostwo, Long> {

  List<Czlonkostwo> findByGrupa_Id(Long grupaId);

  List<Czlonkostwo> findByParafianin_Id(Long parafianinId);
}
