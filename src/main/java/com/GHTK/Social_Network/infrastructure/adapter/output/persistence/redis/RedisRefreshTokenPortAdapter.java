package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.auth.redis.RedisRefreshTokenPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisRefreshTokenPortAdapter extends CrudRedisAdapter<String, String> implements RedisRefreshTokenPort {
  @Value("${application.GHTK.JwtUtils.refreshExpiration}")
  private long refreshExpiration;

  public RedisRefreshTokenPortAdapter(@Qualifier("StringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
    super(redisTemplate);
  }

  @Override
  public void createOrUpdate(String key, String value) {
    super.createOrUpdateWithTTL(key, value, refreshExpiration, TimeUnit.SECONDS);
  }
}
