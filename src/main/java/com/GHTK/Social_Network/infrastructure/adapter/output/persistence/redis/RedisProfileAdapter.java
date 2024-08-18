package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.RedisProfilePort;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisProfileAdapter extends CrudRedisAdapter<String, UserDto> implements RedisProfilePort {
  public RedisProfileAdapter(@Qualifier("profileDtoRedisTemplate") RedisTemplate<String, UserDto> redisTemplate) {
    super(redisTemplate);
  }
}