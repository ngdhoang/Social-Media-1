package com.GHTK.Social_Network.application.service.chat;

import com.GHTK.Social_Network.application.port.input.OfflineOnlineInput;
import com.GHTK.Social_Network.application.port.output.OfflineOnlinePort;
import com.GHTK.Social_Network.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfflineOnlineService implements OfflineOnlineInput {
  private final OfflineOnlinePort offlineOnlinePort;

  @Override
  public void addOnlineUser(User user, String fingerprinting, String sessionId) {
    if (user == null) return;
    offlineOnlinePort.updateOrCreateSessionInRedis(sessionId, fingerprinting, user.getUserId());
  }

  @Override
  public void removeOnlineUser(User user, String sessionId) {
    if (user == null) return;
    log.info("{} is offline", user.getUserEmail());
    offlineOnlinePort.removeSessionInRedis(sessionId);
  }

  @Override
  public boolean isUserOnline(Long userId) {
    return offlineOnlinePort.isOnlineInRedis(userId);
  }
}
