package com.GHTK.Social_Network.infrastructure.adapter.input.websocket.Listener;

import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Objects;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebsocketSubscribeListener {
  private static final String USERDETAILS_KEY = "userDetails";
  private static final String APP_CHANNEL_PATH = "/channel/app/";

  private final SimpMessageSendingOperations messagingTemplate;
  @EventListener
  public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    String sessionId = headerAccessor.getSessionId();
    String subscriptionId = headerAccessor.getSubscriptionId();
    String destination = headerAccessor.getDestination();

    log.info("New subscription attempt - Session ID: {}, Subscription ID: {}, Destination: {}", sessionId, subscriptionId, destination);

    if (destination.startsWith(APP_CHANNEL_PATH)) {
      Long userId = Long.valueOf(destination.replace(APP_CHANNEL_PATH, ""));
      UserDetailsImpl userDetails = getObjectFromAccessor(headerAccessor, USERDETAILS_KEY, UserDetailsImpl.class);
      if (userDetails == null || !userId.equals(userDetails.getUserEntity().getUserId())) {
        log.info("Unauthorized subscription attempt - Session ID: {}, Subscription ID: {}. Disconnecting...", sessionId, subscriptionId);
//        sendErrorMessageAndDisconnect(headerAccessor);
//        throw new CustomException("User Not Permission", HttpStatus.NOT_FOUND);
      }
    }
  }

  private void sendErrorMessageAndDisconnect(StompHeaderAccessor headerAccessor) {
    String sessionId = headerAccessor.getSessionId();

    String errorMessage = "Unauthorized subscription attempt. You will be disconnected.";
    messagingTemplate.convertAndSendToUser(sessionId, "/queue/errors", errorMessage, createHeaders(sessionId));

    StompHeaderAccessor disconnectHeaderAccessor = StompHeaderAccessor.create(StompCommand.DISCONNECT);
    disconnectHeaderAccessor.setSessionId(sessionId);
    messagingTemplate.convertAndSend("/app/disconnect", disconnectHeaderAccessor.getMessageHeaders());

    log.info("Sent disconnect command for session ID: {}", sessionId);
  }


  private <T> T getObjectFromAccessor(StompHeaderAccessor accessor, String key, Class<T> clazz) {
    return clazz.cast(Objects.requireNonNull(accessor.getSessionAttributes()).get(key));
  }

  private Map<String, Object> createHeaders(String sessionId) {
    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
    headerAccessor.setSessionId(sessionId);
    headerAccessor.setLeaveMutable(true);
    return headerAccessor.getMessageHeaders();
  }
}
