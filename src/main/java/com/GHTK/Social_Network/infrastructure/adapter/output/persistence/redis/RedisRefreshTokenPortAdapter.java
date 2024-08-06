package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.auth.RedisRefreshTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisRefreshTokenPortAdapter implements RedisRefreshTokenPort {
  private final RedisTemplate<String, String> refeshTokenRedisTemplate;

  @Override
  public String findByKey(String key) {
    return refeshTokenRedisTemplate.opsForValue().get(key);
  }

  @Override
  public void createOrUpdate(String key, String value) {
    refeshTokenRedisTemplate.opsForValue().set(key, value);
  }

  @Override
  public void deleteByKey(String key) {
    refeshTokenRedisTemplate.delete(key);
  }

  @Override
  public Boolean existsByKey(String key) {
    return refeshTokenRedisTemplate.hasKey(key);
  }
}
