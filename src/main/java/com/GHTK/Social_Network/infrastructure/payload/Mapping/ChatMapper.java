package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.infrastructure.payload.dto.MessageDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.ChatMessageResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMapper {
  Message messageDtoToMessage(MessageDto message);

  default ChatMessageResponse messageToMessageResponse(Message message, EGroupType eGroupType) {
    MessageDto messageDto = MessageDto.builder()
            .tags(message.getTags())
            .msgType(message.getMsgType())
            .content(message.getContent())
            .groupType(eGroupType)
            .groupId(message.getGroupId())
            .replyMsgId(message.getReplyMsgId())
            .build();

    ChatMessageResponse messageResponse = ChatMessageResponse.builder()
            .userId(message.getUserAuthId())
            .msgId(message.getMsgId())
            .message(messageDto)
            .reactionQuantity(message.getReactionQuantity())
            .images(null)
            .build();
    return messageResponse;
  }
}