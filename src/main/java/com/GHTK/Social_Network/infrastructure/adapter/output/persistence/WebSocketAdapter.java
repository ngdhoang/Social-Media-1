package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.WebSocketPort;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketAdapter implements WebSocketPort {
  private final SimpMessageSendingOperations messagingTemplate;

  @Override
  public void SendAndSaveChatMessage(ChatMessageResponse chatMessage, Long receiverId) {
    String destination = String.format("/channel/%s", receiverId);
    try {
      messagingTemplate.convertAndSend(destination, chatMessage);
      // save message into database
    } catch (CustomException e) {
      throw new CustomException("Failed to send message to receiver: " + receiverId, HttpStatus.BAD_GATEWAY);
    }
  }
}
