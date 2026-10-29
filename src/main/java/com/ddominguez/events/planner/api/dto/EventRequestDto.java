package com.ddominguez.events.planner.api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class EventRequestDto {
  @NotBlank(message = "The event name cannot be empty.")
  @Size(max = 255, message = "The event name cannot be more than 255 characters")
  private String name;

  @NotNull(message = "The event date cannot be null")
  @FutureOrPresent(message = "The event date cannot be in the past")
  private LocalDate date;

  @NotBlank(message = "The event location cannot be empty.")
  @Size(max = 255, message = "The event location cannot be more than 255 characters")
  private String location;
}
