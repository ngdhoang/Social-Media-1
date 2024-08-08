package com.GHTK.Social_Network.application.port.output;

public interface OfflineOnlinePort {
  void updateOrCreateSessionInRedis(String session, String fingerprinting, Long userId);

  void removeSessionInRedis(String sessionId);

  boolean isOnlineInRedis(Long userId);

}
