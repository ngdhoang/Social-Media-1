package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.post.RedisImageTemplatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisImageAdapter implements RedisImageTemplatePort {
  RedisTemplate<String, String> imageRedisTemplate;

  @Override
  public String findByKey(String key) {
    return imageRedisTemplate.opsForValue().get(key);
  }

  @Override
  public void createOrUpdate(String key, String value) {
    imageRedisTemplate.opsForValue().set(key, value);
  }

  @Override
  public void deleteByKey(String key) {
    imageRedisTemplate.delete(key);
  }
}
