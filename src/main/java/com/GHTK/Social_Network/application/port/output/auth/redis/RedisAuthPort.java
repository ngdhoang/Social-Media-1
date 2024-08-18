package com.GHTK.Social_Network.application.port.output.auth.redis;

import com.GHTK.Social_Network.application.port.output.template.CrudRedisPort;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.AuthRedisDto;

public interface RedisAuthPort extends CrudRedisPort<String, AuthRedisDto> {
  String REGISTER_TAIL = "_REGISTER_";
  String FORGOT_PASSWORD_TAIL = "_FORGOT_PASSWORD_";
  String DELETE_ACCOUNT_TAIL = "_DELETE_ACCOUNT_";
  String DEVICE_TAIL = "_DEVICE_";
  String DEVICE_CHECK_TAIL = "_DEVICE_CHECK";
}
