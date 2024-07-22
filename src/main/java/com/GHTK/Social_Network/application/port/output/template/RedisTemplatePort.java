package com.GHTK.Social_Network.application.port.output.template;

public interface RedisTemplatePort<K, V> {
  <V> V findByKey(K key);

  void createOrUpdate(K key, V value);

  void deleteByKey(K key);
}