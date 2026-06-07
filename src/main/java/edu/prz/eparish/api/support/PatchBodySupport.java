package edu.prz.eparish.api.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class PatchBodySupport {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private PatchBodySupport() {}

  public static <T> T toValue(JsonNode body, Class<T> type) {
    try {
      return MAPPER.treeToValue(body, type);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid JSON body", e);
    }
  }

  public static boolean hasAnyField(JsonNode body, String... names) {
    for (String name : names) {
      if (body.has(name)) {
        return true;
      }
    }
    return false;
  }
}
