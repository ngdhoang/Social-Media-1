package com.GHTK.Social_Network.application.port.output.auth.redis;

import com.GHTK.Social_Network.application.port.output.template.CrudRedisPort;
import com.GHTK.Social_Network.infrastructure.payload.dto.AccessTokenDto;

import java.util.Map;
import java.util.Set;

public interface RedisAccessTokenPort extends CrudRedisPort<String, AccessTokenDto> {
  String ACCESS_TOKEN_TAIL = "_ACCESS_TOKEN_";
}
