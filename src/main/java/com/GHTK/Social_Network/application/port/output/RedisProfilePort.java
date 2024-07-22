package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.application.port.output.template.RedisTemplatePort;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;

public interface RedisProfilePort extends RedisTemplatePort<String, ProfileDto> {
}
