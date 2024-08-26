package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.application.port.output.template.CrudRedisPort;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserDto;

public interface RedisProfilePort extends CrudRedisPort<String, UserDto> {
    String PROFILE = "_PROFILE_";
}
