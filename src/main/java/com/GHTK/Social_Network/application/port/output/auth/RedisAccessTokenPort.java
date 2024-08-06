package com.GHTK.Social_Network.application.port.output.auth;

import com.GHTK.Social_Network.application.port.output.template.RedisTemplatePort;
import com.GHTK.Social_Network.infrastructure.payload.dto.AccessTokenDto;

import java.util.Map;
import java.util.Set;

public interface RedisAccessTokenPort extends RedisTemplatePort<String, AccessTokenDto> {
  String ACCESS_TOKEN_TAIL = "_ACCESS_TOKEN";

  Set<Map<String, AccessTokenDto>> findAllByTail(String tail);
}
