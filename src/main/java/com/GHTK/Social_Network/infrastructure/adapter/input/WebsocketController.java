package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.WebsocketPortInput;
import com.GHTK.Social_Network.infrastructure.payload.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebsocketController {
  private final WebsocketPortInput websocketPortInput;

  @GetMapping
  public String hello() {
    return "Hello World";
  }

  @MessageMapping("/channel")
  public void addUserToChanel(@Payload MessageDto message) {
    System.out.println(message);
    websocketPortInput.handleIncomingMessage(message);
  }
}
