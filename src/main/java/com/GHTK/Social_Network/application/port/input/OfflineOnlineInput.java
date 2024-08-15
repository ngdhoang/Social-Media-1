package com.GHTK.Social_Network.application.port.input;

import com.GHTK.Social_Network.domain.model.user.User;

public interface OfflineOnlineInput {
  void addOnlineUser(User user, String fingerprinting, String sessionId);

  void removeOnlineUser(User user, String sessionId);

  boolean isUserOnline(User user);
}
