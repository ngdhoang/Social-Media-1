package com.GHTK.Social_Network.application.port.output.auth.redis;

import com.GHTK.Social_Network.application.port.output.template.CrudRedisPort;

public interface RedisRefreshTokenPort extends CrudRedisPort<String, String> {
  String REFRESH_TOKEN = "_REFRESH_TOKEN_";
}
