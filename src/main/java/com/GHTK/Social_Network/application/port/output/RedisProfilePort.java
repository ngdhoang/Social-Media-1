package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.application.port.output.template.RedisTemplatePort;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileStateDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.UserDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.ProfileRedisDto;

public interface RedisProfilePort extends RedisTemplatePort<String, ProfileRedisDto> {
}
