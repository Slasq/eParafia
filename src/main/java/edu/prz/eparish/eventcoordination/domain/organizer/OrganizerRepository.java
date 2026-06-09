package edu.prz.eparish.eventcoordination.domain.organizer;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizerRepository extends JpaRepository<Organizer, Long> {

  List<Organizer> findByEvent_Id(Long eventId);
}
