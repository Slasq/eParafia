package edu.prz.eparish.koordynacjawydarzen.domain.ofiara;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfiaraRepozytorium extends JpaRepository<Ofiara, Long> {

  List<Ofiara> findByWydarzenie_Id(Long wydarzenieId);
}
