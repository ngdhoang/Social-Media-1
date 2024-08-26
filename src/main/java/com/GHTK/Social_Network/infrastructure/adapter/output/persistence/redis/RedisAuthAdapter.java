package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.auth.redis.RedisAuthPort;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.AuthRedisDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public class RedisAuthAdapter extends CrudRedisAdapter<String, AuthRedisDto> implements RedisAuthPort {
  public RedisAuthAdapter(@Qualifier("authRedisTemplate") RedisTemplate<String, AuthRedisDto> redisTemplate) {
    super(redisTemplate);
  }
}
