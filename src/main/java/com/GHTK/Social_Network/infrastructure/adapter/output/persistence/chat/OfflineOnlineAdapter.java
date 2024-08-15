package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.OfflineOnlinePort;
import com.GHTK.Social_Network.application.port.output.RedisSessionWsPort;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.collection.UserCollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OfflineOnlineAdapter implements OfflineOnlinePort {
  private final RedisSessionWsPort redisSessionWsPort;
  private final UserCollectionRepository userCollectionRepository;

  @Override
  public void updateOrCreateSessionInRedis(String session, String fingerprinting, Long userId) {
    String key = session + RedisSessionWsPort.WS + fingerprinting + RedisSessionWsPort.WS + userId;
    redisSessionWsPort.createOrUpdate(key, null);
  }

  @Override
  public void removeSessionInRedis(String sessionId) {
    String key = redisSessionWsPort.getKeyByHeaderKey(sessionId);
    redisSessionWsPort.deleteByKey(key);
  }

  @Override
  public void removeSessionInMongo(Long userId) {
    UserCollection userCollection = userCollectionRepository.findByUserId(userId);
    userCollection.setLastActive(Instant.now());
    userCollectionRepository.save(userCollection);
  }


  @Override
  public boolean isOnlineInRedis(Long userId) {
    return redisSessionWsPort.existsKeyByTailKey(userId);
  }

}
