package com.GHTK.Social_Network.application.port.output.auth;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtPort {
  String generateToken(UserDetails userDetails, String fingerprinting);

  String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, String fingerprinting);

  String generateRefreshToken(UserDetails userDetails, String fingerprinting);

  boolean isTokenValid(String token, UserDetails userDetails);

  String extractUserEmail(String token);

  String extractFingerprinting(String token);

  String parseJwt(StompHeaderAccessor accessor);

  boolean isValidJwtFormat(String token);
}