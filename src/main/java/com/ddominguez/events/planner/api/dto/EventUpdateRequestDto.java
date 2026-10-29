package com.ddominguez.events.planner.api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class EventUpdateRequestDto {
  @Size(min = 1, max = 255, message = "The event name must be between 1 and 255 characters")
  @Pattern(regexp = "(?s).*\\S.*", message = "The event name cannot be blank")
  private String name;

  @FutureOrPresent(message = "The event date cannot be in the past")
  private LocalDate date;

  @Size(min = 1, max = 255, message = "The event location must be between 1 and 255 characters")
  @Pattern(regexp = "(?s).*\\S.*", message = "The event location cannot be blank")
  private String location;

  public boolean hasNoFields() {
    return name == null && date == null && location == null;
  }
}
