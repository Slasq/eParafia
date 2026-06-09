package edu.prz.eparish.parishinformation.domain.record;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParishRecordRepository extends JpaRepository<ParishRecord, Long> {

  Optional<ParishRecord> findByParishioner_Id(Long parishionerId);
}
