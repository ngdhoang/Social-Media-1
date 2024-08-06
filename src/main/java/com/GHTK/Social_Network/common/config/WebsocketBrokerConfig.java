package com.GHTK.Social_Network.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebsocketBrokerConfig implements WebSocketMessageBrokerConfigurer {
  private final HandshakeInterceptor handshakeInterceptor;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/channel", "/queue");
//            .setHeartbeatValue(new long[] {30000, 30000});
    registry.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
            .addEndpoint("/ws")
            .addInterceptors(handshakeInterceptor)
            .setAllowedOrigins("*");
  }

//  @Override
//  public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
//    registration.setSendTimeLimit(15 * 1000) // thời gian tối đa cho một lần gửi thông điệp WebSocket.
//            .setSendBufferSizeLimit(512 * 1024) // giới hạn về kích thước bộ đệm (buffer) được sử dụng khi gửi dữ liệu.
//            .setMessageSizeLimit(128 * 1024); // giới hạn kích thước tối đa của một thông điệp.
//  }
}
