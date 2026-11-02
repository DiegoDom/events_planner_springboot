package com.ddominguez.events.planner.api.dto;

import lombok.Data;

@Data
public class JwtAuthResponseDto {
  private String accessToken;
  private String tokenType = "Bearer";

  public JwtAuthResponseDto(String accessToken) {
    this.accessToken = accessToken;
  }
}
