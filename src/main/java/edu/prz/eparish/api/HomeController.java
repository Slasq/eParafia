package edu.prz.eparish.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

  @GetMapping("/")
  public Map<String, Object> home() {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("application", "eparish");
    body.put("status", "running");
    body.put("apiBase", "/api");
    body.put("h2Console", "/h2-console");
    body.put("endpoints", List.of(
        "GET  /api/parishes",
        "GET  /api/events",
        "POST /api/families",
        "POST /api/parishioners",
        "POST /api/sacrament-administrations",
        "GET  /api/groups",
        "POST /api/employees",
        "POST /api/intentions",
        "POST /api/announcements"));
    return body;
  }
}
