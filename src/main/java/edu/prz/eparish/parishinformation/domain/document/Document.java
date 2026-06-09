package edu.prz.eparish.parishinformation.domain.document;

import edu.prz.eparish.parishinformation.domain.record.ParishRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

@Entity(name = "Document")
@Table(name = "document")
@Data
public class Document {

  @Id
  Long id;

  String type;

  @Column(name = "issue_date")
  LocalDate issueDate;
  String description;

  @ManyToOne
  @JoinColumn(name = "record_id")
  ParishRecord parishRecord;
}
