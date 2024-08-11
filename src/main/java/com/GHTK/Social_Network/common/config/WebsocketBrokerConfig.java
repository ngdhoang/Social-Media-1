package com.GHTK.Social_Network.common.config;

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
  private final WebsocketFilter websocketFilter;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic", "/channel", "/queue");
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");
//    registry.enableSimpleBroker("/channel", "/queue");
////            .setHeartbeatValue(new long[] {30000, 30000});
//    registry.setApplicationDestinationPrefixes("/app");
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

//    registration.interceptors(new ChannelInterceptor() {
//      @Override
//      public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//          String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
//          if (authorizationHeader != null) {
//            final String token = authorizationHeader;
//            final String userEmail = jwtUtils.extractUserEmail(token);
//            if (userEmail != null) {
//              UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
//              if (jwtUtils.isTokenValid(token, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        null, userDetails.getAuthorities());
//                accessor.setUser(authToken);
//              }
//            }
//          }
//          log.debug("error connect");
//        }
//
//        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
//          String targetDestination = "/user/abc/message"; // đường dẫn subscribe dùng để kiểm tra
//          String destination = accessor.getDestination(); // đường dẫn mà user gửi tới
//          String username = "aaa"; // tên người dùng được phép subscribe đến targetDestination
//          Principal authToken = accessor.getUser();
//          if(!targetDestination.equals(destination) && !authToken.getName().equals(username)){
//            throw new CustomException("You can not subscribe to this destination", HttpStatus.NOT_FOUND);
//          }
//        }
//
//        try {
//          accessor.setLeaveMutable(true);
//        } catch (Exception e) {
//          log.error(e.getMessage());
//        }
//        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
//      }
//    });

  }
//  @Override
//  public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
//    registration.setSendTimeLimit(15 * 1000) // thời gian tối đa cho một lần gửi thông điệp WebSocket.
//            .setSendBufferSizeLimit(512 * 1024) // giới hạn về kích thước bộ đệm (buffer) được sử dụng khi gửi dữ liệu.
//            .setMessageSizeLimit(128 * 1024); // giới hạn kích thước tối đa của một thông điệp.
//  }


}
