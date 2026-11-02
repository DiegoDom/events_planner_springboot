package com.ddominguez.events.planner.api.controller;

import com.ddominguez.events.planner.api.dto.JwtAuthResponseDto;
import com.ddominguez.events.planner.api.dto.LoginDto;
import com.ddominguez.events.planner.api.security.jwt.JwtGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final JwtGenerator jwtGenerator;

  @PostMapping("/login")
  public ResponseEntity<JwtAuthResponseDto> login(@RequestBody LoginDto loginDto) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = jwtGenerator.generateJwtToken(authentication);

    return new ResponseEntity<>(new JwtAuthResponseDto(jwt), HttpStatus.OK);
  }
}
