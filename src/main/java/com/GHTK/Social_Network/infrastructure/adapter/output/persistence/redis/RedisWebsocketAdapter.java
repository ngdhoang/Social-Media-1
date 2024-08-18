package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.chat.redis.RedisWebsocketPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisWebsocketAdapter extends CrudRedisAdapter<String, String> implements RedisWebsocketPort {
  @Value("${application.GHTK.websocket.timeRing}")
  private long timeRing;

  private RedisTemplate<String, String> sessionWsTemplate;

  public RedisWebsocketAdapter(@Qualifier("StringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
    super(redisTemplate);
    this.sessionWsTemplate = redisTemplate;
  }

  @Override
  public boolean existsKeyByTailKey(Long userId) {
    return Boolean.TRUE.equals(sessionWsTemplate.hasKey("*" + RedisWebsocketPort.WEBSOCKET + userId));
  }

  @Override
  public String getKeyByHead(String headerKey) {
    Set<String> keys = sessionWsTemplate.keys(headerKey + RedisWebsocketPort.WEBSOCKET + "*");
    if (!Objects.requireNonNull(keys).isEmpty()) {
      return keys.iterator().next();
    }
    return null;
  }

  @Override
  public void createOrUpdateCallVideo(String key, String value) {
    super.createOrUpdateWithTTL(key, value, timeRing, TimeUnit.SECONDS);
  }
}
