package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.collection.chat.EMessageType;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.domain.collection.chat.ReactionMessages;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.EMessageTypeCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.ReactionMessagesCollection;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapperETD {

  @Mapping(target = "reactionMsgs", ignore = true)
  MessageCollection messageToMessageCollection(Message message);

  @Mapping(target = "reactionMsgs", ignore = true)
  @Mapping(target = "images", source = "images")
  Message messageCollectionToMessage(MessageCollection messageCollection);

  ReactionMessagesCollection mapReactionMessages(ReactionMessages reactionMessages);

  ReactionMessages mapReactionMessagesCollection(ReactionMessagesCollection reactionMessagesCollection);

  EMessageTypeCollection mapEMessageType(EMessageType messageType);

  EMessageType mapEMessageTypeCollection(EMessageTypeCollection messageTypeCollection);

  @AfterMapping
  default void setReactionMessagesCollection(@MappingTarget MessageCollection target, Message source) {
    if (source.getReactionMsgs() != null) {
      target.setReactionMsgs(mapReactionMessages(source.getReactionMsgs()));
    }
  }

  @AfterMapping
  default void setReactionMessages(@MappingTarget Message target, MessageCollection source) {
    if (source.getReactionMsgs() != null) {
      target.setReactionMsgs(mapReactionMessagesCollection(source.getReactionMsgs()));
    }
  }
}