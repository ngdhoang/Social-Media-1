package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.post.RedisImagePort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisImageAdapter extends CrudRedisAdapter<String, String> implements RedisImagePort {
  public RedisImageAdapter(@Qualifier("StringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
    super(redisTemplate);
  }
}
