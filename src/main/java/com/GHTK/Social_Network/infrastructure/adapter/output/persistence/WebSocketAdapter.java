package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.WebSocketPort;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.MessageRepository;
import com.GHTK.Social_Network.infrastructure.mapper.ChatMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketAdapter implements WebSocketPort {
  private final SimpMessageSendingOperations messagingTemplate;
  private final MessageRepository messageRepository;
  private final ChatMapperETD chatMapperETD;

  @Override
  public void SendAndSaveChatMessage(Message message, Long sendId, Long receiverId) {
    String destination = String.format("/channel/%s", receiverId);
    messagingTemplate.convertAndSend(destination, message);

    // save message into database
    MessageCollection messageCollection = chatMapperETD.messageToMessageCollection(message);
    messageRepository.save(messageCollection);
  }
}