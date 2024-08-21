package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.infrastructure.payload.dto.chat.MessageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.chat.ChatMessageResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMapper {
  Message messageDtoToMessage(MessageDto message);

  default ChatMessageResponse messageToMessageResponse(Message message, UserBasicDto userBasicDto, EGroupType eGroupType) {
    MessageDto messageDto = MessageDto.builder()
            .tags(message.getTags())
            .msgType(message.getMsgType())
            .content(message.getContent())
            .groupType(eGroupType)
            .groupId(message.getGroupId())
            .replyMsgId(message.getReplyMsgId())
            .createAt(message.getCreateAt())
            .build();

    ChatMessageResponse messageResponse = ChatMessageResponse.builder()
            .msgId(message.getId())
            .user(userBasicDto)
            .message(messageDto)
            .reactionQuantity(message.getReactionQuantity())
            .images(message.getImages())
            .build();
    return messageResponse;
  }
}