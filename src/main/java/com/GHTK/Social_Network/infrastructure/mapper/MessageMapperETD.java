package com.GHTK.Social_Network.infrastructure.mapper;

import com.GHTK.Social_Network.domain.collection.chat.EMessageType;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.domain.collection.chat.ReactionMessages;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.EMessageTypeCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.ReactionMessagesCollection;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapperETD {
  @Mapping(target = "id", source = "id", qualifiedByName = "stringToObjectId")
  @Mapping(target = "reactionMsgs", source = "reactionMsgs", qualifiedByName = "reactionMessagesToReactionMessagesCollection")
  MessageCollection messageToMessageCollection(Message message);

  @Mapping(target = "id", source = "id", qualifiedByName = "objectIdToString")
  @Mapping(target = "reactionMsgs", source = "reactionMsgs", qualifiedByName = "reactionMessagesCollectionToReactionMessages")
  @Mapping(target = "images", source = "images")
  @Mapping(target = "createAt", source = "createAt")
  Message messageCollectionToMessage(MessageCollection messageCollection);

  @Mapping(target = "reactionType", source = "reactionType")
  @Mapping(target = "userId", source = "userId")
  ReactionMessagesCollection mapReactionMessages(ReactionMessages reactionMessages);

  @Mapping(target = "reactionType", source = "reactionType")
  @Mapping(target = "userId", source = "userId")
  ReactionMessages mapReactionMessagesCollection(ReactionMessagesCollection reactionMessagesCollection);

  EMessageTypeCollection mapEMessageType(EMessageType messageType);

  EMessageType mapEMessageTypeCollection(EMessageTypeCollection messageTypeCollection);

  @Named("reactionMessagesToReactionMessagesCollection")
  default List<ReactionMessagesCollection> reactionMessagesToReactionMessagesCollection(List<ReactionMessages> reactionMessages) {
    if (reactionMessages == null) {
      return null;
    }
    return reactionMessages.stream()
            .map(this::mapReactionMessages)
            .collect(Collectors.toList());
  }

  @Named("reactionMessagesCollectionToReactionMessages")
  default List<ReactionMessages> reactionMessagesCollectionToReactionMessages(List<ReactionMessagesCollection> reactionMessagesCollections) {
    if (reactionMessagesCollections == null) {
      return null;
    }
    return reactionMessagesCollections.stream()
            .map(this::mapReactionMessagesCollection)
            .collect(Collectors.toList());
  }

  @Named("stringToObjectId")
  default ObjectId stringToObjectId(String id) {
    return id == null ? null : new ObjectId(id);
  }

  @Named("objectIdToString")
  default String objectIdToString(ObjectId id) {
    return id == null ? null : id.toHexString();
  }
}