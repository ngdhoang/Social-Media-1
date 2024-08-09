package com.GHTK.Social_Network.infrastructure.adapter.input.websocket;

import com.GHTK.Social_Network.application.port.output.RedisSessionWsPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebsocketFilter implements ChannelInterceptor {
  private final RedisSessionWsPort redisSessionWsPort;
  private final AuthPort authPort;
  private final UserMapper userMapper;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    String sessionId = accessor.getSessionId();
    if (StompCommand.SEND == accessor.getCommand()) {
      String key = redisSessionWsPort.getKeyByHeaderKey(sessionId);

      String[] parts = key.split(RedisSessionWsPort.WS);
      String userId = parts.length > 0 ? parts[parts.length - 1] : null;

      User user = authPort.getUserById(Long.valueOf(userId));

      WebsocketContextHolder.setContext(userMapper.userToUserBasicDto(user));
    }
    return message;
  }

}
