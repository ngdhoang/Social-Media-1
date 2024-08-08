package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.OfflineOnlineInput;
import com.GHTK.Social_Network.application.port.output.OfflineOnlinePort;
import com.GHTK.Social_Network.domain.UserWsDetails;
import com.GHTK.Social_Network.domain.model.user.User;
import com.GHTK.Social_Network.infrastructure.adapter.input.websocket.WebsocketContextHolder;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfflineOnlineService implements OfflineOnlineInput {
  private final OfflineOnlinePort offlineOnlinePort;
  private final UserMapper userMapper;

  @Override
  public void addOnlineUser(User user, String fingerprinting, String sessionId) {
    if (user == null) return;
    UserWsDetails userWsDetails = new UserWsDetails(
            userMapper.userToUserBasicDto(user),
            fingerprinting,
            sessionId
    );
    System.out.println("===========");
    System.out.println(userWsDetails);
    System.out.println("===========");
    WebsocketContextHolder.setContext(userWsDetails);
    offlineOnlinePort.updateOrCreateSessionInRedis(user.getUserId(), userWsDetails);
  }

  @Override
  public void removeOnlineUser(User user, String sessionId) {
    if (user == null) return;
    log.info("{} is offline", user.getUserEmail());
    offlineOnlinePort.removeSessionInRedis(user.getUserId(), sessionId);
  }

  @Override
  public boolean isUserOnline(User user) {
    if (user == null) return false;
    UserWsDetails sessionWsDto = WebsocketContextHolder.getContext();
    return offlineOnlinePort.getSessionInRedisByKey(user.getUserId(), sessionWsDto.getSession()) != null;
  }
}
