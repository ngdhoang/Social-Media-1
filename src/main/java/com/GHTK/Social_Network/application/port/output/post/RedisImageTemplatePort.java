package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.application.port.output.template.RedisTemplatePort;

import java.util.Set;

public interface RedisImageTemplatePort extends RedisTemplatePort<String, String> {
  Set<String> findAllByKeys(String key);
}
