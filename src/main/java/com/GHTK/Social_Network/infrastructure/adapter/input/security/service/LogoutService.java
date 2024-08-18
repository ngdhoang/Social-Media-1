package com.GHTK.Social_Network.infrastructure.adapter.input.security.service;

import com.GHTK.Social_Network.application.port.output.RedisProfilePort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.auth.JwtPort;
import com.GHTK.Social_Network.application.port.output.auth.redis.RedisAccessTokenPort;
import com.GHTK.Social_Network.application.port.output.auth.redis.RedisRefreshTokenPort;
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
  private final UserDetailsService userDetailsService;

  private final JwtPort jwtPort;
  private final AuthPort authPort;

  private final RedisAccessTokenPort redisAccessTokenPort;
  private final RedisRefreshTokenPort redisRefreshTokenPort;
  private final RedisProfilePort redisProfilePort;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    String token = extractTokenFromRequest(request);
    if (token == null) {
      return;
    }

    validateToken(token);
    String userEmail = jwtPort.extractUserEmail(token);
    if (userEmail == null) {
      return;
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
    AccessTokenDto accessTokenDto = authPort.findByToken(token, userEmail);

    if (jwtPort.isTokenValid(token, userDetails)) {
      performLogout(accessTokenDto, userDetails);
    }
  }

  private String extractTokenFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return null;
    }
    return authHeader.substring(7);
  }

  private void validateToken(String token) {
    if (!jwtPort.isValidJwtFormat(token)) {
      throw new CustomException("Invalid token format", HttpStatus.UNAUTHORIZED);
    }
  }

  private void performLogout(AccessTokenDto accessTokenDto, UserDetails userDetails) {
    redisAccessTokenPort.deleteAllByHead(accessTokenDto.getFingerprinting());
    redisRefreshTokenPort.deleteAllByTail(buildRefreshTokenKey(accessTokenDto, userDetails));
    redisProfilePort.deleteAllByHead(String.valueOf(accessTokenDto.getUserId()));
    SecurityContextHolder.clearContext();
  }

  private String buildRefreshTokenKey(AccessTokenDto accessTokenDto, UserDetails userDetails) {
    return RedisRefreshTokenPort.REFRESH_TOKEN
            + accessTokenDto.getFingerprinting()
            + RedisRefreshTokenPort.REFRESH_TOKEN
            + userDetails.getUsername();
  }
}