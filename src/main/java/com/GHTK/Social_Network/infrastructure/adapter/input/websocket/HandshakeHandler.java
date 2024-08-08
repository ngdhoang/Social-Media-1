package com.GHTK.Social_Network.infrastructure.adapter.input.websocket;

import com.GHTK.Social_Network.application.port.output.auth.JwtPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HandshakeHandler implements HandshakeInterceptor {
  private final JwtPort jwtUtils;
  private final UserDetailsService userDetailsService;

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                 WebSocketHandler wsHandler, Map<String, Object> attributes) {
    try {
      String token = extractTokenFromQuery(request);
      UserDetails userDetails = validateAndProcessToken(token);
      attributes.put("userDetails", userDetails);
      attributes.put("fingerprinting", jwtUtils.extractFingerprinting(token));
      return true;
    } catch (CustomException e) {
      log.error("Handshake failed: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                             WebSocketHandler wsHandler, Exception exception) {
  }

  private String extractTokenFromQuery(ServerHttpRequest request) throws CustomException {
    String query = request.getURI().getQuery();
    if (query == null) {
      throw new CustomException("Missing query parameters", HttpStatus.BAD_REQUEST);
    }
    return extractTokenFromParams(query.split("&"));
  }

  private String extractTokenFromParams(String[] params) throws CustomException {
    for (String param : params) {
      String[] keyValue = param.split("=");
      if ("token".equals(keyValue[0]) && keyValue.length > 1) {
        return keyValue[1];
      }
    }
    throw new CustomException("Token not found in query", HttpStatus.BAD_REQUEST);
  }

  private UserDetails validateAndProcessToken(String token) throws CustomException {
    if (!jwtUtils.isValidJwtFormat(token)) {
      throw new CustomException("Invalid token format", HttpStatus.UNAUTHORIZED);
    }

    String userEmail = jwtUtils.extractUserEmail(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

    if (!jwtUtils.isTokenValid(token, userDetails)) {
      throw new CustomException("Invalid token", HttpStatus.FORBIDDEN);
    }

    return userDetails;
  }
}