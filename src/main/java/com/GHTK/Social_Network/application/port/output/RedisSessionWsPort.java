package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.application.port.output.template.RedisTemplatePort;
import com.GHTK.Social_Network.infrastructure.payload.dto.SessionWsDto;

public interface RedisSessionWsPort extends RedisTemplatePort<String, SessionWsDto> {
  String WS_INFO = "_WS_INFO";
}
