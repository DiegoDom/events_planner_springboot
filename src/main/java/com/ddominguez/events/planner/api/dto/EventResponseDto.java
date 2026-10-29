package com.ddominguez.events.planner.api.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class EventResponseDto {
  private Long id;
  private String name;
  private LocalDate date;
  private String location;
}
