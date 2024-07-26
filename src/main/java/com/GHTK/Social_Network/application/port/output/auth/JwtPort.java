package com.GHTK.Social_Network.application.port.output.auth;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtPort {
  String generateToken(UserDetails userDetails);

  String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

  String generateRefreshToken(UserDetails userDetails);

  boolean isTokenValid(String token, UserDetails userDetails);

  String extractUserEmail(String token);
}