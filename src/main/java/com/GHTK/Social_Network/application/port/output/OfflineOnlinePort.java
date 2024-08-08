package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.domain.UserWsDetails;

public interface OfflineOnlinePort {
  void updateOrCreateSessionInRedis(Long userId, UserWsDetails sessionWsDto);

  void removeSessionInRedis(Long userId, String sessionId);

  Object getSessionInRedisByKey(Long userId, String sessionId);
}
