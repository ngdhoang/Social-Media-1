package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.chat.WebsocketPort;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.service.UserDetailsImpl;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WebsocketAdapter implements WebsocketPort {
  @Override
  public UserDetailsImpl extractUserDetails(StompHeaderAccessor accessor) {
    return (UserDetailsImpl) extractAll(accessor).get("userDetails");
  }

  @Override
  public UserDetailsImpl extractUserDetails(Message<?> message) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    return extractUserDetails(accessor);
  }

  @Override
  public Object extractByKey(String key, StompHeaderAccessor accessor) {
    return extractAll(accessor).get(key);
  }

  @Override
  public Map<String, Object> extractAll(StompHeaderAccessor accessor) {
    return accessor.getSessionAttributes();
  }
}
