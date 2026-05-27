package edu.prz.eparish.koordynacjawydarzen.domain.uczestnik;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UczestnikRepozytorium extends JpaRepository<Uczestnik, Long> {

  List<Uczestnik> findByWydarzenie_Id(Long wydarzenieId);
}
