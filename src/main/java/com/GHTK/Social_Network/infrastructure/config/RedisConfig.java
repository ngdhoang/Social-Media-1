package com.GHTK.Social_Network.infrastructure.config;

import com.GHTK.Social_Network.infrastructure.payload.dto.PostDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
  @Value("${application.GHTK.redis.host}")
  private String redisHost;

  @Value("${application.GHTK.redis.port}")
  private int redisPort;

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisHost, redisPort));
  }

  @Bean
  @Primary
  public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<Object, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    return template;
  }

  @Bean
  public RedisTemplate<String, ProfileDto> profileDtoRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, ProfileDto> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(ProfileDto.class));
    return template;
  }

  @Bean
  public RedisTemplate<String, PostDto> postDtoRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, PostDto> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(ProfileDto.class));
    return template;
  }
}