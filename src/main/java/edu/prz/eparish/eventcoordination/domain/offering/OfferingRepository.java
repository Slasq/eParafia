package edu.prz.eparish.eventcoordination.domain.offering;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferingRepository extends JpaRepository<Offering, Long> {

  List<Offering> findByEvent_Id(Long eventId);
}
