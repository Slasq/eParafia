package edu.prz.eparish.koordynacjawydarzen.domain.ogloszenie;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OgloszenieRepozytorium extends JpaRepository<Ogloszenie, Long> {

  List<Ogloszenie> findByWydarzenie_Id(Long wydarzenieId);
}
