package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.Map;

public interface WebsocketPort {
  UserDetailsImpl extractUserDetails(StompHeaderAccessor accessor);

  UserDetailsImpl extractUserDetails(Message<?> message);

  Object extractByKey(String key, StompHeaderAccessor accessor);

  Map<String, Object> extractAll(StompHeaderAccessor accessor);
}
