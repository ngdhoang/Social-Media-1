package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.redis;

import com.GHTK.Social_Network.application.port.output.template.CrudRedisPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
public class CrudRedisAdapter<K, V> implements CrudRedisPort<K, V> {
  private final RedisTemplate<K, V> redisTemplate;

  public CrudRedisAdapter(RedisTemplate<K, V> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public V findByKey(K key) {
    return redisTemplate.opsForValue().get(key);
  }

  @Override
  public void createOrUpdate(K key, V value) {
    redisTemplate.opsForValue().set(key, value);
  }

  @Override
  public void createOrUpdateWithTTL(K key, V value, long timeout, TimeUnit unit) {
    redisTemplate.opsForValue().set(key, value, timeout, unit);
  }

  @Override
  public void deleteByKey(K key) {
    redisTemplate.delete(key);
  }

  @Override
  public Boolean existsByKey(K key) {
    return redisTemplate.hasKey(key);
  }

  @Override
  public List<K> getKeysByPattern(String pattern) {
    Set<K> keys = redisTemplate.keys((K) pattern);
    return (keys != null && !keys.isEmpty()) ? new ArrayList<>(keys) : Collections.emptyList();
  }

  @Override
  public List<K> getKeysByHead(String key) {
    return getKeysByPattern(key + "*");
  }

  @Override
  public List<K> getKeysByTail(String key) {
    return getKeysByPattern("*" + key);
  }

  @Override
  public List<K> getKeysByBody(String key) {
    return getKeysByPattern("*" + key + "*");
  }

  @Override
  public void deleteAllByPattern(String pattern) {
    getKeysByPattern(pattern).forEach(redisTemplate::delete);
  }

  @Override
  public void deleteAllByHead(String key) {
    deleteAllByPattern(key + "*");
  }

  @Override
  public void deleteAllByTail(String key) {
    deleteAllByPattern("*" + key);
  }

  @Override
  public void deleteAllByBody(String key) {
    deleteAllByPattern("*" + key + "*");
  }

  @Override
  public Set<Map<K, V>> getKeyValueByPattern(String pattern) {
    List<K> keys = getKeysByPattern(pattern);
    Set<Map<K, V>> result = new HashSet<>();
    for (K key : keys) {
      V value = findByKey(key);
      if (value != null) {
        result.add(Collections.singletonMap(key, value));
      }
    }
    return result;
  }
}