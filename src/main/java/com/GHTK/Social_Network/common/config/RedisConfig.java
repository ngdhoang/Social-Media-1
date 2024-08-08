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
    RedisTemplate<String, UserDto> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(UserDto.class));
    return template;
  }

  @Bean
  public RedisTemplate<String, AuthRedisDto> authRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, AuthRedisDto> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(AuthRedisDto.class));
    return template;
  }

  @Bean
  public RedisTemplate<String, String> imageRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
    return template;
  }

  @Bean
  public RedisTemplate<String, String> sessionWsTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
    return template;
  }

  @Bean
  public RedisTemplate<String, String> refeshTokenRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
    return template;
  }

  @Bean
  public RedisTemplate<String, AccessTokenDto> accessTokenRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, AccessTokenDto> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new Jackson2JsonRedisSerializer<>(AccessTokenDto.class));
    return template;
  }
}