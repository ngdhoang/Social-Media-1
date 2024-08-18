package com.GHTK.Social_Network.common.config;

import com.GHTK.Social_Network.infrastructure.adapter.input.websocket.ContextAwareThreadPoolTaskScheduler;
import com.GHTK.Social_Network.infrastructure.adapter.input.websocket.WebsocketFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebsocketBrokerConfig implements WebSocketMessageBrokerConfigurer {
  private final HandshakeInterceptor handshakeInterceptor;
  private final ContextAwareThreadPoolTaskScheduler contextAwareThreadPoolTaskScheduler;
  private final WebsocketFilter websocketFilter;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic", "/channel", "/queue")
            .setHeartbeatValue(new long[]{25000, 30000})
            .setTaskScheduler(contextAwareThreadPoolTaskScheduler);
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
            .addEndpoint("/ws")
            .addInterceptors(handshakeInterceptor)
            .setAllowedOrigins("*");
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(websocketFilter);
  }
}