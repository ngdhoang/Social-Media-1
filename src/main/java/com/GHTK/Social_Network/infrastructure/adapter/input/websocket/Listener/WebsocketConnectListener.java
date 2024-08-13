package com.GHTK.Social_Network.infrastructure.adapter.input.websocket.Listener;

import com.GHTK.Social_Network.application.port.input.OfflineOnlineInput;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebsocketConnectListener {
  private static final String USERDETAILS_KEY = "userDetails";
  private static final String FINGERPRINTING_KEY = "fingerprinting";

  private final OfflineOnlineInput offlineOnlineInput;
  private final AuthPort authPort;
  private final SimpMessageSendingOperations messagingTemplate;

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = accessor.getSessionId();
    log.info("New WebSocket connection, sessionId: {}", sessionId);

    org.springframework.messaging.Message<?> connectMessage = (org.springframework.messaging.Message<?>) accessor.getHeader(SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER);
    if (connectMessage != null) {
      StompHeaderAccessor connectHeaders = StompHeaderAccessor.wrap(connectMessage);
      UserDetailsImpl userDetails = getObjectFromAccessor(connectHeaders, USERDETAILS_KEY, UserDetailsImpl.class);


      String fingerprinting = getObjectFromAccessor(connectHeaders, FINGERPRINTING_KEY, String.class);
      if (userDetails != null && fingerprinting != null) {
        log.info("User connected: {}", userDetails.getUsername());
        offlineOnlineInput.addOnlineUser(authPort.findByEmail(userDetails.getUsername()).orElse(null), "1234", sessionId);

        String destinationSub = "/channel/app/" + userDetails.getUserEntity().getUserId();
        messagingTemplate.convertAndSend(destinationSub, "You are automatically subscribed!");

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

    offlineOnlineInput.removeOnlineUser(sessionId);
  }

  private <T> T getObjectFromAccessor(StompHeaderAccessor accessor, String key, Class<T> clazz) {
    return clazz.cast(Objects.requireNonNull(accessor.getSessionAttributes()).get(key));
  }
}