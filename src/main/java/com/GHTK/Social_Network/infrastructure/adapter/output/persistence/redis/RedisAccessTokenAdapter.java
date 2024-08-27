package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.auth.redis.RedisAccessTokenPort;
import com.GHTK.Social_Network.infrastructure.payload.dto.AccessTokenDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisAccessTokenAdapter extends CrudRedisAdapter<String, AccessTokenDto> implements RedisAccessTokenPort {
  @Value("${application.GHTK.JwtUtils.jwtExpiration}")
  private long jwtExpiration;

  public RedisAccessTokenAdapter(@Qualifier("accessTokenRedisTemplate") RedisTemplate<String, AccessTokenDto> redisTemplate) {
    super(redisTemplate);
  }

  @Override
  public void createOrUpdate(String key, AccessTokenDto value) {
    super.createOrUpdateWithTTL(key, value, jwtExpiration, TimeUnit.SECONDS);
  }
}