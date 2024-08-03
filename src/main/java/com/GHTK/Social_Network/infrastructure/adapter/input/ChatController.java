package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.WebSocketPortInput;
import com.GHTK.Social_Network.infrastructure.payload.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
  private final WebSocketPortInput webSocketPortInput;

  @GetMapping
  public String hello() {
    return "Hello World";
  }

  @MessageMapping("/channel")
  public ChatMessageDto addUserToChanel(@Payload ChatMessageDto chatMessage) {
    webSocketPortInput.handleIncomingMessage(chatMessage);
    return null;
  }
}
