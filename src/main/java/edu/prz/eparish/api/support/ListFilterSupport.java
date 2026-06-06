package edu.prz.eparish.api.support;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class ListFilterSupport {

  private ListFilterSupport() {}

  @SafeVarargs
  public static <T> List<T> filter(List<T> source, Predicate<T>... predicates) {
    Stream<T> stream = source.stream();
    for (Predicate<T> predicate : predicates) {
      if (predicate != null) {
        stream = stream.filter(predicate);
      }
    }
    return stream.toList();
  }

  public static <T> Predicate<T> eq(Object value, java.util.function.Function<T, ?> getter) {
    return value == null ? null : item -> Objects.equals(value, getter.apply(item));
  }

  public static <T> Predicate<T> eqLong(Long value, java.util.function.Function<T, Long> getter) {
    return value == null ? null : item -> {
      Long fieldValue = getter.apply(item);
      return fieldValue != null && fieldValue.equals(value);
    };
  }

  public static <T> Predicate<T> containsIgnoreCase(String value, java.util.function.Function<T, String> getter) {
    if (value == null || value.isBlank()) {
      return null;
    }
    String needle = value.toLowerCase();
    return item -> {
      String fieldValue = getter.apply(item);
      return fieldValue != null && fieldValue.toLowerCase().contains(needle);
    };
  }
}
