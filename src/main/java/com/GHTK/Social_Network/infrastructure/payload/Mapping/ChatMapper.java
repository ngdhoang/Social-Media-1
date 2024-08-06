package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.model.chat.Message;
import com.GHTK.Social_Network.domain.model.chat.EMessageType;
import com.GHTK.Social_Network.infrastructure.payload.dto.MessageDto;
import com.GHTK.Social_Network.domain.model.EChatMessageType;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ChatMapper {
  @Mapping(target = "groupId", source = "groupId", qualifiedByName = "longToString")
  @Mapping(target = "msgType", source = "msgType")
  @Mapping(target = "userAuthId", ignore = true)
  @Mapping(target = "reaction", ignore = true)
  @Mapping(target = "reactionMsgs", ignore = true)
  @Mapping(target = "createAt", ignore = true)
  Message messageDtoToMessage(MessageDto messageDto);

  @Mapping(target = "groupId", source = "groupId", qualifiedByName = "stringToLong")
  @Mapping(target = "msgType", source = "msgType")
  @Mapping(target = "groupType", ignore = true)
  MessageDto messageToMessageDto(Message message);

  @Named("longToString")
  default String longToString(Long value) {
    return value != null ? value.toString() : null;
  }

  @Named("stringToLong")
  default Long stringToLong(String value) {
    return value != null ? Long.parseLong(value) : null;
  }

  default EMessageType map(EChatMessageType value) {
    return value != null ? EMessageType.valueOf(value.name()) : null;
  }

  default EChatMessageType map(EMessageType value) {
    return value != null ? EChatMessageType.valueOf(value.name()) : null;
  }

  @AfterMapping
  default void setDefaultValues(@MappingTarget Message message) {
    if (message.getReaction() == null) {
      message.setReaction(new java.util.ArrayList<>());
    }
    if (message.getCreateAt() == null) {
      message.setCreateAt(new java.util.Date());
    }
  }
}