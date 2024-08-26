package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.OfflineOnlinePort;
import com.GHTK.Social_Network.application.port.output.chat.redis.RedisWebsocketPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.UserCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.collection.UserCollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OfflineOnlineAdapter implements OfflineOnlinePort {
  private final RedisWebsocketPort redisWebsocketPort;
  private final UserCollectionRepository userCollectionRepository;

  @Override
  public void updateOrCreateSessionInRedis(String session, String fingerprinting, Long userId) {
    String key = session + RedisWebsocketPort.WEBSOCKET + fingerprinting + RedisWebsocketPort.WEBSOCKET + userId;
    redisWebsocketPort.createOrUpdate(key, null);
  }

  @Override
  public void removeSessionInRedis(String sessionId) {
    String key = redisWebsocketPort.getKeyByHead(sessionId);
    redisWebsocketPort.deleteByKey(key);
  }

  @Override
  public void offlineInMongo(Long userId) {
    if (redisWebsocketPort.existsKeyByTailKey(userId)) {
      return;
    }
    UserCollection userCollection = userCollectionRepository.findByUserId(userId);
    userCollection.setLastActive(Instant.now());
    userCollectionRepository.save(userCollection);
  }

  @Override
  public void onlineInMongo(Long userId) {

  }


  @Override
  public boolean isOnlineInRedis(Long userId) {
    return redisWebsocketPort.existsKeyByTailKey(userId);
  }

  @Override
  public Long getUserIdBySessionInRedis(String session) {
    String key = redisWebsocketPort.getKeyByHead(session);
    String[] parts = key.split(RedisWebsocketPort.WEBSOCKET);

    if (parts.length > 0) {
      return Long.valueOf(parts[parts.length - 1]);
    } else {
      throw new CustomException("Invalid Session", HttpStatus.BAD_GATEWAY);
    }
  }

}
