package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.RedisSessionWsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisSessionWsAdapter implements RedisSessionWsPort {
  private final RedisTemplate<String, String> sessionWsTemplate;

  @Override
  public String findByKey(String key) {
    return sessionWsTemplate.opsForValue().get(key);
  }

  @Override
  public void createOrUpdate(String key, String value) {
    sessionWsTemplate.opsForValue().set(key, value);
  }

  @Override
  public void deleteByKey(String key) {
    key += RedisSessionWsPort.WS;
    sessionWsTemplate.delete(key);
  }

  @Override
  public Boolean existsByKey(String key) {
    key += RedisSessionWsPort.WS;
    return sessionWsTemplate.hasKey(key);
  }
  @Override
  public String getKeyByHeaderKey(String headerKey) {
    Set<String> keys = sessionWsTemplate.keys(headerKey + RedisSessionWsPort.WS + "*");
    if (!Objects.requireNonNull(keys).isEmpty()) {
      return keys.iterator().next();
    }
    return null;
  }

  @Override
  public boolean existsKeyByTailKey(Long userId) {
    return Boolean.TRUE.equals(sessionWsTemplate.hasKey("*" + RedisSessionWsPort.WS + userId));
  }
}
