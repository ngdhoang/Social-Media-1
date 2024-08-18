package com.GHTK.Social_Network.application.port.input.chat;

import com.GHTK.Social_Network.domain.model.user.User;

public interface OfflineOnlineInput {
  void addOnlineUser(User user, String fingerprinting, String sessionId);

  void removeOnlineUser(String sessionId);

  boolean isUserOnline(Long userId);
}
