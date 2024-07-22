package com.GHTK.Social_Network.application.port.output;

import org.hibernate.mapping.Map;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtPort {
  String generateToken(UserDetails userDetails);

  String generateToken(Map extraClaims, UserDetails userDetails);

  String generateRefreshToken(UserDetails userDetails);

  boolean isTokenValid(String token, UserDetails userDetails);

  String extractUserEmail(String token);
}
