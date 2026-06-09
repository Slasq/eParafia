package edu.prz.eparish.eventcoordination.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParishEventRepository extends JpaRepository<ParishEvent, Long> {

}
