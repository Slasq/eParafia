package edu.prz.eparish.eventcoordination.domain.announcement;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

  List<Announcement> findByEvent_Id(Long eventId);
}
