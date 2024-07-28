package com.GHTK.Social_Network.application.port.output.auth;

import com.GHTK.Social_Network.application.port.output.template.RedisTemplatePort;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.AuthRedisDto;

public interface RedisAuthPort extends RedisTemplatePort<String, AuthRedisDto> {
}
