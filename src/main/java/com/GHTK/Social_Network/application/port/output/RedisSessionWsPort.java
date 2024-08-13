package com.GHTK.Social_Network.application.port.output;

import com.GHTK.Social_Network.application.port.output.template.RedisTemplatePort;

public interface RedisSessionWsPort extends RedisTemplatePort<String, String> {
  String WS = "_WEBSOCKET_";

  String getKeyByHeaderKey(String headerKey);

  boolean existsKeyByTailKey(Long userId);
}
