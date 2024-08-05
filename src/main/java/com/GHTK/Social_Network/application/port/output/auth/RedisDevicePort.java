package com.GHTK.Social_Network.application.port.output.auth;

import com.GHTK.Social_Network.application.port.output.template.RedisTemplatePort;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.DeviceRedisDto;

public interface RedisDevicePort extends RedisTemplatePort<String, DeviceRedisDto> {
}
