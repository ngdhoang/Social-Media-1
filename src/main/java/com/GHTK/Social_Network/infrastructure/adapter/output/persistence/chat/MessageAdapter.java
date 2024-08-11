package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.chat.MessagePort;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.MessageRepository;
import com.GHTK.Social_Network.infrastructure.mapper.MessageMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageAdapter implements MessagePort {
  private final MessageRepository messageRepository;

  private final MessageMapperETD messageMapperETD;

  @Override
  public Message getMessageById(String messageId) {
    MessageCollection messageCollection = messageRepository.findById(messageId).orElse(null);
    if (messageCollection == null) {
      return null;
    }
    return messageMapperETD.messageCollectionToMessage(messageCollection);
  }

  @Override
  public Message saveMessage(Message newMessage) {
    return messageMapperETD.messageCollectionToMessage(
            messageRepository.save(messageMapperETD.messageToMessageCollection(newMessage))
    );
  }
}
