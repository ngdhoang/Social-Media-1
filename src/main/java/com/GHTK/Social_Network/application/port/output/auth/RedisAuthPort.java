package com.GHTK.Social_Network.application.port.output.auth;

import com.GHTK.Social_Network.infrastructure.payload.dto.AuthRedisDto;

public interface RedisAuthPort {
  AuthRedisDto findByKey(String key);

  AuthRedisDto createOrUpdate(String key, AuthRedisDto authRedisDto);

  void deleteByKey(String key);
}
