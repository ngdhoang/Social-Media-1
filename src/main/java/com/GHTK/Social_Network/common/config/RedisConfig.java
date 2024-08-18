package com.GHTK.Social_Network.common.config;

import com.GHTK.Social_Network.infrastructure.payload.dto.AccessTokenDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.redis.AuthRedisDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserDto;
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
  public RedisTemplate<String, UserDto> profileDtoRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    return createRedisTemplate(redisConnectionFactory, UserDto.class);
  }

  @Bean
  public RedisTemplate<String, AuthRedisDto> authRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    return createRedisTemplate(redisConnectionFactory, AuthRedisDto.class);
  }

  @Bean
  public RedisTemplate<String, String> StringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    return createRedisTemplate(redisConnectionFactory, String.class);
  }

  @Bean
  public RedisTemplate<String, AccessTokenDto> accessTokenRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    return createRedisTemplate(redisConnectionFactory, AccessTokenDto.class);
  }

  private <T> RedisTemplate<String, T> createRedisTemplate(RedisConnectionFactory redisConnectionFactory, Class<T> valueType) {
    RedisTemplate<String, T> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(valueType));
    return template;
  }
}