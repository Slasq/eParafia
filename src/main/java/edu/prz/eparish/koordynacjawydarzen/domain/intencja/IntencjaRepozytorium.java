package edu.prz.eparish.koordynacjawydarzen.domain.intencja;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntencjaRepozytorium extends JpaRepository<Intencja, Long> {

  List<Intencja> findByWydarzenie_Id(Long wydarzenieId);
}
