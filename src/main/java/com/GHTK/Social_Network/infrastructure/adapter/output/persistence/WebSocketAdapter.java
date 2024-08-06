package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.WebSocketPort;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.MessageRepository;
import com.GHTK.Social_Network.infrastructure.mapper.ChatMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;
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
  public void SendAndSaveChatMessage(ChatMessageResponse chatMessage, Long receiverId) {
    String destination = String.format("/channel/%s", receiverId);
    messagingTemplate.convertAndSend(destination, chatMessage);
    // save message into database

//    MessageCollection messageCollection = chatMapperETD.messageDtoToMessageCollection(chatMessage.getChatMessage());
//    messageRepository.save(messageCollection);
  }
}
