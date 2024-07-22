package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.auth.RedisAuthPort;
import com.GHTK.Social_Network.infrastructure.payload.dto.AuthRedisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisAuthAdapter implements RedisAuthPort {
  private final RedisTemplate<String, AuthRedisDto> authRedisTemplate;

  @Override
  public AuthRedisDto findByKey(String key) {
    return authRedisTemplate.opsForValue().get(key);
  }

  @Override
  public AuthRedisDto createOrUpdate(String key, AuthRedisDto authRedisDto) {
    authRedisTemplate.opsForValue().set(key, authRedisDto);
    return this.findByKey(key);
  }

  @Override
  public void deleteByKey(String key) {
    authRedisTemplate.delete(key);
  }
}
