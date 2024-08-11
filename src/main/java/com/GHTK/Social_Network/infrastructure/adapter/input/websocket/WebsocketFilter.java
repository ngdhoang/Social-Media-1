package com.GHTK.Social_Network.infrastructure.adapter.input.websocket;

import com.GHTK.Social_Network.application.port.output.RedisSessionWsPort;
import com.GHTK.Social_Network.application.port.output.WebsocketPort;
import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
  private final WebsocketPort websocketPort;
  private final UserMapper userMapper;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    String sessionId = accessor.getSessionId();

    if (StompCommand.SEND == accessor.getCommand()) {
      sendPreManagerHandler(sessionId);
    }

    if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) { // Chưa được set giá trị trong context holder
      Long userId = websocketPort.extractUserDetails(accessor).getUserEntity().getUserId();
      String destination = accessor.getDestination();
      connectPreManagerHandler(userId, destination);
    }

    return message;
  }

  private void sendPreManagerHandler(String sessionId) {
    String key = redisSessionWsPort.getKeyByHeaderKey(sessionId);

    String[] parts = key.split(RedisSessionWsPort.WS);
    String userId = parts.length > 0 ? parts[parts.length - 1] : null;

    User user = authPort.getUserById(Long.valueOf(userId));
    WebsocketContextHolder.setContext(userMapper.userToUserBasicDto(user));
  }

  private void connectPreManagerHandler(Long userId, String destination) {
    String CHANNEL_USER_TARGET = "/channel/app/";
    if (destination.startsWith(CHANNEL_USER_TARGET)) {
      Long userTargetId = Long.valueOf(destination.substring(CHANNEL_USER_TARGET.length()));
      if (!userTargetId.equals(userId)) {
        throw new CustomException("You can not subscribe to this destination", HttpStatus.NOT_FOUND);
      }
    }
  }
}
