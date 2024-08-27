package com.GHTK.Social_Network.application.port.output.template;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface CrudRedisPort<K, V> {
  V findByKey(K key);

  void createOrUpdate(K key, V value);

  boolean createOrUpdateWithTTL(K key, V value, long timeout, TimeUnit unit);

  void deleteByKey(K key);

  Boolean existsByKey(K key);

  List<K> getKeysByPattern(String pattern);

  Set<Map<K, V>> getKeyValueByPattern(String pattern);

  List<K> getKeysByHead(String key);

  List<K> getKeysByTail(String key);

  List<K> getKeysByBody(String key);

  void deleteAllByPattern(String pattern);

  void deleteAllByHead(String key);

  void deleteAllByTail(String key);

  void deleteAllByBody(String key);
}