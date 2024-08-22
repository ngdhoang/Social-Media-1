package com.GHTK.Social_Network.application.port.output.chat.redis;

import com.GHTK.Social_Network.application.port.output.template.CrudRedisPort;

public interface RedisWebsocketPort extends CrudRedisPort<String, String> {
  String WEBSOCKET = "_WEBSOCKET_";

  String RING = "_RING_CALL_VIDEO_";

  boolean existsKeyByTailKey(Long userId);

  String getKeyByHead(String headerKey);

  void createOrUpdateCallVideo(String key, String value);
}
