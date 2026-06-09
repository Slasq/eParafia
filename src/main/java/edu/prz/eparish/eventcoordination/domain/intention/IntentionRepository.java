package edu.prz.eparish.eventcoordination.domain.intention;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntentionRepository extends JpaRepository<Intention, Long> {

  List<Intention> findByEvent_Id(Long eventId);
}
