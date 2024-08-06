package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HandshakeHandlerAdapter implements HandshakeInterceptor {
  @Override
  public boolean beforeHandshake(
          ServerHttpRequest request,
          ServerHttpResponse response,
          WebSocketHandler wsHandler,
          Map<String, Object> attributes
  ) throws Exception {
    String token = "";
    HttpHeaders headers = request.getHeaders(); // get header for request
    List<String> authHeader = headers.get("Authorization");
    if (authHeader != null && !authHeader.isEmpty()) {
      token = authHeader.get(0).replace("Bearer ", "");
    }

    log.info(token);

    return true;
  }

  @Override
  public void afterHandshake(
          ServerHttpRequest request,
          ServerHttpResponse response,
          WebSocketHandler wsHandler,
          Exception exception
  ) {

  }
}
