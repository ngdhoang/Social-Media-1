package com.GHTK.Social_Network.application.port.output.auth.redis;

import com.GHTK.Social_Network.application.port.output.template.CrudRedisPort;

public interface RedisLockPort extends CrudRedisPort<String, String> {
    String POST_LOCK = "_POST_LOCK_";
    String COMMENT_LOCK = "_COMMENT_LOCK_";
}
