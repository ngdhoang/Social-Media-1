package com.GHTK.Social_Network.infrastructure.adapter.input.websocket;

import com.GHTK.Social_Network.application.port.output.auth.JwtPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.adapter.input.security.jwt.JwtUtils;
import com.GHTK.Social_Network.infrastructure.adapter.output.persistence.user.AuthAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebsocketTokenFilter implements ChannelInterceptor {
  private final JwtPort jwtUtils;
  private final UserDetailsService userDetailsService;
  private final AuthAdapter tokenRepository;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (StompCommand.CONNECT == accessor.getCommand()) {

      String jwt = jwtUtils.parseJwt(accessor);
      if (jwt == null) {
        throw new CustomException("Empty token", HttpStatus.UNAUTHORIZED);
      }

      try {
        String userEmail = jwtUtils.extractUserEmail(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        var tokenOptional = tokenRepository.findByToken(jwt, userEmail);
        if (tokenOptional == null || tokenOptional.isExpired() || tokenOptional.isRevoked()) {
          throw new CustomException("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        if (jwtUtils.isTokenValid(jwt, userDetails)) {
          UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken(
                          userDetails, null, userDetails.getAuthorities());
          accessor.setUser(authentication);
        }
      } catch (Exception e) {
        throw new CustomException("An error occurred while processing the token", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    return message;
  }
}
