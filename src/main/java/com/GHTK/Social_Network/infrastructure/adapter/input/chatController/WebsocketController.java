package com.GHTK.Social_Network.infrastructure.adapter.input.chatController;

import com.GHTK.Social_Network.application.port.input.chat.WebsocketPortInput;
import com.GHTK.Social_Network.infrastructure.payload.dto.chat.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebsocketController {
  private final WebsocketPortInput websocketPortInput;

  @MessageMapping("/channel")
  public void addUserToChanel(@Payload MessageDto message) {
    websocketPortInput.handleIncomingMessage(message);
  }
}
