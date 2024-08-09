package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.WebsocketPort;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.EMessageType;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.infrastructure.adapter.input.websocket.WebsocketContextHolder;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.MessageRepository;
import com.GHTK.Social_Network.infrastructure.mapper.ChatMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ChatMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.MessageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebsocketAdapter implements WebsocketPort {
  private final SimpMessageSendingOperations messagingTemplate;
  private final MessageRepository messageRepository;

  private final ChatMapperETD chatMapperETD;
  private final ChatMapper chatMapper;

  @Override
  public void sendAndNotSave(ChatMessageResponse message, Long receiverId) {
    String destination = String.format("/app/channel/%s", receiverId);
    messagingTemplate.convertAndSend(destination, message);
  }

  @Override
  public void sendAndSave(EGroupType groupType, Message message, Long sendId, Long receiverId) {
    String destination = String.format("/app/channel/%s", receiverId);

    MessageCollection messageCollection = chatMapperETD.messageToMessageCollection(message);
    messageRepository.save(messageCollection);

    ChatMessageResponse messageSend = chatMapper.messageToMessageResponse(message, groupType);
    messagingTemplate.convertAndSend(destination, messageSend);
  }

  @Override
  public void sendErrorForMe(String error, Long userReceiveId) {
    MessageDto messageErrorDto = MessageDto.builder()
            .groupType(EGroupType.PERSONAL)
            .content(error)
            .msgType(EMessageType.ERROR)
            .build();
    ChatMessageResponse messageError = ChatMessageResponse.builder()
            .userId(userReceiveId)
            .message(messageErrorDto)
            .build();
    sendAndNotSave(messageError, userReceiveId);
  }

  @Override
  public UserBasicDto getUserAuth() {
    return WebsocketContextHolder.getContext();
  }
}
