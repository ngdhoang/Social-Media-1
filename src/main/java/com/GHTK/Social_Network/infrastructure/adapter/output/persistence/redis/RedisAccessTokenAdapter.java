package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.auth.RedisAccessTokenPort;
import com.GHTK.Social_Network.infrastructure.payload.dto.AccessTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RedisAccessTokenAdapter implements RedisAccessTokenPort {
  private final RedisTemplate<String, AccessTokenDto> accessTokenRedisTemplate;

  @Override
  public AccessTokenDto findByKey(String key) {
    return accessTokenRedisTemplate.opsForValue().get(key);
  }

  @Override
  public void createOrUpdate(String key, AccessTokenDto value) {
    accessTokenRedisTemplate.opsForValue().set(key, value);
  }

  @Override
  public void deleteByKey(String key) {
    accessTokenRedisTemplate.delete(key);
  }

  @Override
  public Boolean existsByKey(String key) {
    return accessTokenRedisTemplate.hasKey(key);
  }

  @Override
  public Set<Map<String, AccessTokenDto>> findAllByTail(String tail) {
    Set<String> keys = accessTokenRedisTemplate.keys("*" + tail);
    if (keys == null || keys.isEmpty()) {
      return Collections.emptySet();
    }

    Set<Map<String, AccessTokenDto>> result = new HashSet<>();
    for (String key : keys) {
      AccessTokenDto accessToken = accessTokenRedisTemplate.opsForValue().get(key);
      if (accessToken != null) {
        Map<String, AccessTokenDto> entry = new HashMap<>();
        entry.put(key, accessToken);
        result.add(entry);
      }
    }
    return result;
  }

}
