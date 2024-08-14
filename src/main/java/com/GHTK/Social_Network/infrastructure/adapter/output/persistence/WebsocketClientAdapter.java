package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.auth.AuthPort;
import com.GHTK.Social_Network.application.port.output.chat.WebsocketClientPort;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.EMessageType;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.infrastructure.adapter.input.websocket.WebsocketContextHolder;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.MessageRepository;
import com.GHTK.Social_Network.infrastructure.mapper.MessageMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.ChatMapper;
import com.GHTK.Social_Network.infrastructure.payload.Mapping.UserMapper;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageReplyResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebsocketClientAdapter implements WebsocketClientPort {
  private final SimpMessageSendingOperations messagingTemplate;
  private final MessageRepository messageRepository;
  private final AuthPort authPort;

  private final MessageMapperETD messageMapperETD;
  private final UserMapper userMapper;
  private final ChatMapper chatMapper;

  @Override
  public void sendUserAndNotSave(Message message, String destination) {
    messagingTemplate.convertAndSend(destination, message);
  }

  @Override
  public void sendUserAndSave(EGroupType groupType, Message message, String destination) {
    message = saveMessage(message);

    ChatMessageResponse messageSend = chatMapper.messageToMessageResponse(message, userMapper.userToUserBasicDto(authPort.getUserById(message.getUserAuthId())), groupType);
    messagingTemplate.convertAndSend(destination, messageSend);
  }

  @Override
  public void sendRelyUserAndSave(EGroupType groupType, Message message, Message messageQuote, String destination) {
    message = saveMessage(message);
    messagingTemplate.convertAndSend(destination, messageToChatMessageReplyResponse(message, messageQuote, groupType));
  }

  @Override
  public void sendUserError(String error, Long userReceiveId) {
    Message message = Message.builder()
            .msgType(EMessageType.ERROR)
            .createAt(Instant.now())
            .content(error)
            .userAuthId(userReceiveId)
            .build();

    sendUserAndNotSave(message, "/channel/app/" + userReceiveId);
  }

  @Override
  public void sendListUserAndSave(Message messages, List<Long> receiverIds) {
    messages = saveMessage(messages);
    sendListUserAndNotSave(messages, receiverIds);
  }

  @Override
  public void sendReplyListUserAndSave(Message message, Message messageQuote, List<Long> receiverIds) {
    message = saveMessage(message);

    for (Long receiverId : receiverIds) {
      String destination = "/channel/app/" + receiverId;
      ChatMessageReplyResponse chatMessageReplyResponse = messageToChatMessageReplyResponse(message, messageQuote, EGroupType.PERSONAL);
      messagingTemplate.convertAndSend(destination, chatMessageReplyResponse);
    }
  }

  @Override
  public void sendListUserAndNotSave(Message messages, List<Long> receiverIds) {
    receiverIds.forEach(receiverId -> {
      String destination = "/channel/app/" + receiverId;
      sendUserAndNotSave(messages, destination);
    });
  }

  @Override
  public void sendListUserError(String error, List<Long> receiveIds) {
    receiveIds.forEach(receiveId -> sendUserError(error, receiveId));
  }

  @Override
  public UserBasicDto getUserAuth() {
    return WebsocketContextHolder.getContext();
  }

  private Message saveMessage(Message message) {
    MessageCollection messageCollection = messageMapperETD.messageToMessageCollection(message);
    messageCollection.setCreateAt(Instant.now());
    return messageMapperETD.messageCollectionToMessage(messageRepository.save(messageCollection));
  }

  private ChatMessageReplyResponse messageToChatMessageReplyResponse(Message message, Message messageQuote, EGroupType groupType) {
    ChatMessageResponse chatMessageQuote = chatMapper.messageToMessageResponse(messageQuote, userMapper.userToUserBasicDto(authPort.getUserById(messageQuote.getUserAuthId())), groupType);
    ChatMessageResponse chatMessageSend = chatMapper.messageToMessageResponse(message, userMapper.userToUserBasicDto(authPort.getUserById(message.getUserAuthId())), groupType);
    return new ChatMessageReplyResponse(chatMessageQuote, chatMessageSend);
  }
}
