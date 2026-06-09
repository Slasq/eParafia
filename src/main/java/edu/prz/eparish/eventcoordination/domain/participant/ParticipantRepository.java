package edu.prz.eparish.eventcoordination.domain.participant;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

  List<Participant> findByEvent_Id(Long eventId);
}
