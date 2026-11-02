package com.ddominguez.events.planner.api.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtGenerator {
  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.expiration}")
  private long expirationTime;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  public String generateJwtToken(Authentication authentication) {
    String username = authentication.getName();
    Date currentDate = new Date();
    Date expirationDate = new Date(currentDate.getTime() + expirationTime);

    String token =
        Jwts.builder()
            .subject(username)
            .issuedAt(currentDate)
            .expiration(expirationDate)
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
    return token;
  }

  public String getUsernameFromJwt(String token) {
    Claims claims =
        Jwts.parser()
            .verifyWith((SecretKey) getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    return claims.getSubject();
  }

  public boolean validateToken(String token) {
    try {
      // La forma moderna para validación también
      Jwts.parser()
          .verifyWith((SecretKey) getSigningKey())
          .build()
          .parseSignedClaims(token); // Aquí solo nos interesa que no lance excepción
      return true;
    } catch (MalformedJwtException e) {
      System.out.println("Invalid JWT token: " + e.getMessage());
    } catch (ExpiredJwtException e) {
      System.out.println("JWT token is expired: " + e.getMessage());
    } catch (UnsupportedJwtException e) {
      System.out.println("JWT token is unsupported: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.out.println("JWT claims string is empty: " + e.getMessage());
    }

    return false;
  }
}
