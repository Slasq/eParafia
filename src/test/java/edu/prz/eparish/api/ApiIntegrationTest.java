package edu.prz.eparish.api;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void homeReturnsApiInfo() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.apiBase").value("/api"));
  }

  @Test
  void listParishesReturnsSeedData() throws Exception {
    mockMvc.perform(get("/api/parishes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1));
  }

  @Test
  void seedEventExistsForIntentionAndAnnouncementTests() throws Exception {
    mockMvc.perform(get("/api/events"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1));
  }

  @Test
  void addFamilyMatchesBrunoTest() throws Exception {
    mockMvc.perform(post("/api/families")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"familyName":"Kowalscy","memberCount":4}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.familyName").value("Kowalscy"))
        .andExpect(jsonPath("$.memberCount").value(4));
  }

  @Test
  void createGroupMatchesBrunoTest() throws Exception {
    mockMvc.perform(post("/api/groups")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "name":"Oaza Mlodziezowa",
                  "description":"Grupa spotykajaca sie w piatki o 18:00 w salce",
                  "supervisor":"Michal Nowak"
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Oaza Mlodziezowa"));
  }

  @Test
  void addEmployeeMatchesBrunoTest() throws Exception {
    mockMvc.perform(post("/api/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "firstName":"Marek",
                  "lastName":"Zalewski",
                  "parishId":1,
                  "positionId":1
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.parishId").value(1));
  }

  @Test
  void addIntentionMatchesBrunoTest() throws Exception {
    mockMvc.perform(post("/api/intentions")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "content":"For the health and blessing of the Kowalski family",
                  "date":"2026-05-30",
                  "donor":"Jan Kowalski",
                  "eventId":1
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.eventId").value(1))
        .andExpect(jsonPath("$.id").value(greaterThan(0)));
  }

  @Test
  void addAnnouncementMatchesBrunoTest() throws Exception {
    mockMvc.perform(post("/api/announcements")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "content":"Zbiorka na remont dachu odbedzie sie bezposrednio po zakonczeniu mszy.",
                  "eventId":1
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.eventId").value(1));
  }

  @Test
  void registerSacramentMatchesBrunoTest() throws Exception {
    mockMvc.perform(post("/api/sacrament-administrations")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "administrationDate":"2026-05-25",
                  "parishionerId":1,
                  "priestId":1,
                  "sacramentId":1
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.parishionerId").value(1));
  }
}
