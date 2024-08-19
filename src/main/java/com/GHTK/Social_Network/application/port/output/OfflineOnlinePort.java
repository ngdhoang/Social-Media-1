package com.GHTK.Social_Network.application.port.output;

public interface OfflineOnlinePort {
  void updateOrCreateSessionInRedis(String session, String fingerprinting, Long userId);

  void removeSessionInRedis(String sessionId);

  void removeSessionInMongo(Long userId);

  boolean isOnlineInRedis(Long userId);
}
