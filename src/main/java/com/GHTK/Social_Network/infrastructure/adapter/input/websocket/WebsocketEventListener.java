package com.GHTK.Social_Network.infrastructure.adapter.input.websocket;

import com.GHTK.Social_Network.application.port.input.OfflineOnlineInput;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebsocketEventListener {
  private final OfflineOnlineInput offlineOnlineInput;
  private final AuthPort authPort;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = accessor.getSessionId();
    log.info("New WebSocket connection, sessionId: {}", sessionId);

    org.springframework.messaging.Message<?> connectMessage = (org.springframework.messaging.Message<?>) accessor.getHeader(SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER);
    if (connectMessage != null) {
      StompHeaderAccessor connectHeaders = StompHeaderAccessor.wrap(connectMessage);
      UserDetailsImpl userDetails = (UserDetailsImpl) connectHeaders.getSessionAttributes().get("userDetails");
      String fingerprinting = (String) connectHeaders.getSessionAttributes().get("fingerprinting");;
      fingerprinting = "1234567"; // fake fingering ...
      if (userDetails != null && fingerprinting != null) {
        log.info("User connected: {}", userDetails.getUsername());
        offlineOnlineInput.addOnlineUser(authPort.findByEmail(userDetails.getUsername()).orElse(null), "1234" , sessionId);
      } else {
        log.warn("Received websocket connect event without user details, sessionId: {}", sessionId);
      }
    } else {
      log.warn("Connect message is null for sessionId: {}", sessionId);
    }
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = accessor.getSessionId();

    // Get simpConnectMessage from disconnect event
    org.springframework.messaging.Message<?> connectMessage = (org.springframework.messaging.Message<?>) accessor.getHeader(SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER);
    if (connectMessage != null) {
      StompHeaderAccessor connectHeaders = StompHeaderAccessor.wrap(connectMessage);
      UserDetailsImpl userDetails = (UserDetailsImpl) connectHeaders.getSessionAttributes().get("userDetails");
      if (userDetails != null) {
        log.info("User disconnected: {}, sessionId: {}", userDetails.getUsername(), sessionId);
        offlineOnlineInput.removeOnlineUser(authPort.findByEmail(userDetails.getUsername()).orElse(null), sessionId);
      } else {
        log.warn("Received websocket disconnect event without user details, sessionId: {}", sessionId);
      }
    } else {
      log.warn("Connect message is null for disconnect event, sessionId: {}", sessionId);
    }

    WebsocketContextHolder.clearContext();
  }
}