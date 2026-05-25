package edu.prz.eparish.api.support;

import java.util.function.Function;
import org.springframework.data.jpa.repository.JpaRepository;

public final class EntityIds {

  private EntityIds() {}

  public static <E> Long nextId(JpaRepository<E, Long> repository, Function<E, Long> idExtractor) {
    return repository.findAll().stream()
        .map(idExtractor)
        .max(Long::compareTo)
        .orElse(0L) + 1;
  }
}
