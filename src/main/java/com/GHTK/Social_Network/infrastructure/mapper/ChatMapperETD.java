package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.model.chat.Message;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import com.GHTK.Social_Network.infrastructure.payload.dto.MessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapperETD {
  @Mapping(target = "msgType", source = "msgType")
  MessageCollection messageToMessageCollection(Message message);

  @Mapping(target = "msgType", source = "msgType")
  Message messageCollectionToMessage(MessageCollection messageCollection);

  @Mapping(target = "msgType", source = "msgType")
  MessageCollection messageDtoToMessageCollection(MessageDto messageDto);

  @Mapping(target = "msgType", source = "msgType")
  MessageDto messageCollectionToMessageDto(MessageCollection messageCollection);
}