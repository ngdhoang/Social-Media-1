package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.RedisProfilePort;

import com.GHTK.Social_Network.infrastructure.payload.dto.user.FieldVisibilityDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RedisProfileAdapter implements RedisProfilePort {
  private final RedisTemplate<String, UserDto> profileDtoRedisTemplate;

  @Override
  public UserDto findByKey(String key) {
    return profileDtoRedisTemplate.opsForValue().get(key);
  }

  @Override
  public void createOrUpdate(String key, UserDto value) {
    profileDtoRedisTemplate.opsForValue().set(key, value);
  }

  @Override
  public void deleteByKey(String key) {
    profileDtoRedisTemplate.delete(key);
  }

  @Override
  public Boolean existsByKey(String key) {
    return profileDtoRedisTemplate.hasKey(key);
  }
}