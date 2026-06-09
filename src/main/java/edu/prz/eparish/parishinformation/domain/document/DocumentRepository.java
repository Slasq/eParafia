package edu.prz.eparish.parishinformation.domain.document;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

  List<Document> findByParishRecord_Id(Long recordId);
}
