package com.GHTK.Social_Network.infrastructure.adapter.input.security.service;

import com.GHTK.Social_Network.infrastructure.adapter.output.persistence.AuthRepositoryPortImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
  private final AuthRepositoryPortImpl tokenRepository;

  @Override
  public void logout(
          HttpServletRequest request,
          HttpServletResponse response,
          Authentication authentication
  ) {
    final String authHeader = request.getHeader("Authorization");
    if (authHeader.isEmpty() || !authHeader.startsWith("Bearer "))
      return;
    final String jwt = authHeader.substring(7);
    var storedToken = tokenRepository.findByToken(jwt).orElse(null);
    if (storedToken != null){
      storedToken.setRevoked(false);
      storedToken.setExpired(false);
      tokenRepository.saveToken(storedToken);
      SecurityContextHolder.clearContext();
    }
  }
}
