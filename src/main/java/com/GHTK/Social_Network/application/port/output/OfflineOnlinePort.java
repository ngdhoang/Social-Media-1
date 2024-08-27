package com.GHTK.Social_Network.application.port.output;

public interface OfflineOnlinePort {
  void updateOrCreateSessionInRedis(String session, String fingerprinting, Long userId);

  void removeSessionInRedis(String sessionId);

  void offlineInMongo(Long userId);

  void onlineInMongo(Long userId);

  boolean isOnlineInRedis(Long userId);

  Long getUserIdBySessionInRedis(String session);
}
