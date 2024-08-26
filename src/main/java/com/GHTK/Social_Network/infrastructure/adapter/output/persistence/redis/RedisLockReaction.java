package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.auth.redis.RedisLockPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisLockReaction extends CrudRedisAdapter<String, String> implements RedisLockPort {
    public RedisLockReaction(@Qualifier("lockReactionRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate);
    }
}
