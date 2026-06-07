package edu.prz.eparish.api.support;

import java.util.Map;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public final class PatchBodySupport {

  private static final ObjectMapper MAPPER = JsonMapper.builder().build();

  private PatchBodySupport() {}

  public static <T> T toValue(Map<String, Object> body, Class<T> type) {
    return MAPPER.convertValue(body, type);
  }

  public static boolean hasAnyField(Map<String, Object> body, String... names) {
    for (String name : names) {
      if (body.containsKey(name)) {
        return true;
      }
    }
    return false;
  }

  public static Long nullableLong(Map<String, Object> body, String key) {
    if (!body.containsKey(key)) {
      return null;
    }
    Object value = body.get(key);
    if (value == null) {
      return null;
    }
    if (value instanceof Number number) {
      return number.longValue();
    }
    return Long.parseLong(value.toString());
  }
}
