package com.GHTK.Social_Network.infrastructure.adapter.input.security.service;

import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.auth.JwtPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.dto.AccessTokenDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
  private final JwtPort jwtPort;
  private final UserDetailsService userDetailsService;
  private final AuthPort authPort;

  @Override
  public void logout(
          HttpServletRequest request,
          HttpServletResponse response,
          Authentication authentication
  ) {
    final String authHeader = request.getHeader("Authorization");
    if (!authHeader.startsWith("Bearer "))
      return;
    final String token = authHeader.substring(7);
    if (!jwtPort.isValidJwtFormat(token)) {
      throw new CustomException("Invalid token format", HttpStatus.UNAUTHORIZED);
    }
    final String userEmail = jwtPort.extractUserEmail(token);
    if (userEmail != null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
      AccessTokenDto accessTokenDto = authPort.findByToken(token, userEmail);
      if (jwtPort.isTokenValid(token, userDetails)) {
        accessTokenDto.setRevoked(false);
        accessTokenDto.setExpired(false);
        authPort.saveAccessTokenInRedis(token, accessTokenDto);
        SecurityContextHolder.clearContext();
      }
    }
  }
}
