package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.RedisProfilePort;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.ProfileRedisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisProfileAdapter implements RedisProfilePort {
  private final RedisTemplate<String, ProfileRedisDto> profileDtoRedisTemplate;

  @Override
  public ProfileRedisDto findByKey(String key) {
    return profileDtoRedisTemplate.opsForValue().get(key);
  }

  @Override
  public void createOrUpdate(String key, ProfileRedisDto value) {
    profileDtoRedisTemplate.opsForValue().set(key, value);
  }

  @Override
  public void deleteByKey(String key) {
    profileDtoRedisTemplate.delete(key);
  }

  @Override
  public String formatKey(String key) {
    return "";
  }

  @Override
  public Boolean existsByKey(String key) {
    return profileDtoRedisTemplate.hasKey(key);
  }
}