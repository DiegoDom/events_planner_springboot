package com.ddominguez.events.planner.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddominguez.events.planner.api.domain.Event;
import com.ddominguez.events.planner.api.repository.EventRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EventControllerTest {

  private static final String EVENTS_URL = "/api/v1/events";

  @Autowired private MockMvc mockMvc;

  @Autowired private EventRepository eventRepository;

  @Test
  void createReturnsBadRequestWhenDateIsNotIso8601() throws Exception {
    String body =
        """
        {"name":"Conference","date":"31/10/2026","location":"Madrid"}
        """;

    mockMvc
        .perform(post(EVENTS_URL).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.errors.date").value(containsString("ISO-8601")));
  }

  @Test
  void createReturnsBadRequestWhenDateIsInThePast() throws Exception {
    String body =
        """
        {"name":"Conference","date":"2020-01-01","location":"Madrid"}
        """;

    mockMvc
        .perform(post(EVENTS_URL).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.date").value("The event date cannot be in the past"));
  }

  @Test
  void updateOnlyChangesProvidedFields() throws Exception {
    Event saved = saveEvent("Original name", LocalDate.now().plusMonths(1), "Madrid");

    String body =
        """
        {"name":"Updated name"}
        """;

    mockMvc
        .perform(
            put(EVENTS_URL + "/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saved.getId().intValue()))
        .andExpect(jsonPath("$.name").value("Updated name"))
        .andExpect(jsonPath("$.date").value(saved.getDate().toString()))
        .andExpect(jsonPath("$.location").value("Madrid"));
  }

  @Test
  void updateReturnsBadRequestWhenDateIsNotIso8601() throws Exception {
    Event saved = saveEvent("Original name", LocalDate.now().plusMonths(1), "Madrid");

    String body =
        """
        {"date":"31/10/2026"}
        """;

    mockMvc
        .perform(
            put(EVENTS_URL + "/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.date").value(containsString("ISO-8601")));
  }

  @Test
  void updateReturnsBadRequestWhenNoFieldsAreProvided() throws Exception {
    Event saved = saveEvent("Original name", LocalDate.now().plusMonths(1), "Madrid");

    mockMvc
        .perform(
            put(EVENTS_URL + "/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("At least one field must be provided"));
  }

  @Test
  void updateReturnsNotFoundWhenEventDoesNotExist() throws Exception {
    String body =
        """
        {"name":"Updated name"}
        """;

    mockMvc
        .perform(put(EVENTS_URL + "/999999").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isNotFound());
  }

  @Test
  void findByIdReturnsBadRequestWhenIdIsNotNumeric() throws Exception {
    mockMvc
        .perform(get(EVENTS_URL + "/abc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors.id").value(containsString("Invalid value")));
  }

  private Event saveEvent(String name, LocalDate date, String location) {
    Event event = new Event();
    event.setName(name);
    event.setDate(date);
    event.setLocation(location);
    return eventRepository.save(event);
  }
}
