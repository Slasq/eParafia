package edu.prz.eparish.koordynacjawydarzen.domain.organizator;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizatorRepozytorium extends JpaRepository<Organizator, Long> {

  List<Organizator> findByWydarzenie_Id(Long wydarzenieId);
}
