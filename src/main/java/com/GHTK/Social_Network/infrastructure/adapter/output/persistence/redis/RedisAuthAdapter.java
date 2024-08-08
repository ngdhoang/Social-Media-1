package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.auth.RedisAuthPort;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.AuthRedisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisAuthAdapter implements RedisAuthPort {
  private final RedisTemplate<String, AuthRedisDto> authRedisTemplate;

  @Override
  public AuthRedisDto findByKey(String key) {
    return authRedisTemplate.opsForValue().get(key);
  }

  @Override
  public void createOrUpdate(String key, AuthRedisDto authRedisDto) {
    authRedisTemplate.opsForValue().set(key, authRedisDto);
  }

  @Override
  public void deleteByKey(String key) {
    authRedisTemplate.delete(key);
  }

  @Override
  public Boolean existsByKey(String key) {
    return authRedisTemplate.hasKey(key);
  }

  @Override
  public void deleteAllByTail(String tail) {
    Set<String> keys = authRedisTemplate.keys("*" + tail);

    if (keys != null && !keys.isEmpty()) {
      authRedisTemplate.delete(keys);
    }
  }
}
