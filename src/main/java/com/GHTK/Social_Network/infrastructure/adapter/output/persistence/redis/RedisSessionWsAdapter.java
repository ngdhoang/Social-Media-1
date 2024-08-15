package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.RedisSessionWsPort;
import com.GHTK.Social_Network.infrastructure.payload.dto.SessionWsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSessionWsAdapter implements RedisSessionWsPort {
    private final RedisTemplate<String, SessionWsDto> sessionWsTemplate;

  @Override
  public SessionWsDto findByKey(String key) {
    key += RedisSessionWsPort.WS_INFO;
    return sessionWsTemplate.opsForValue().get(key);
  }

  @Override
  public void createOrUpdate(String key, SessionWsDto value) {
    key += RedisSessionWsPort.WS_INFO;
    sessionWsTemplate.opsForValue().set(key, value);
  }

  @Override
  public void deleteByKey(String key) {
    key += RedisSessionWsPort.WS_INFO;
    sessionWsTemplate.delete(key);
  }

  @Override
  public Boolean existsByKey(String key) {
    key += RedisSessionWsPort.WS_INFO;
    return sessionWsTemplate.hasKey(key);
  }
}
