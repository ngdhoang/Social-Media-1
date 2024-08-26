package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.chat;

import com.GHTK.Social_Network.application.port.output.chat.MessagePort;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.ReactionMessagesCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionTypeEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.MessageRepository;
import com.GHTK.Social_Network.infrastructure.mapper.MessageMapperETD;
import com.GHTK.Social_Network.infrastructure.mapper.ReactionTypeMapperETD;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageAdapter implements MessagePort {
  private final MessageRepository messageRepository;

  private final MessageMapperETD messageMapperETD;
  private final ReactionTypeMapperETD reactionTypeMapperETD;

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

  @Override
  public Message getLastMessageByGroupId(String groupId) {
    PaginationRequest paginationRequest = new PaginationRequest();
    paginationRequest.setPage(0);
    paginationRequest.setSize(1);
//    paginationRequest.setOrderBy("asc");
    List<Pair<Message, Message>> message = getMessagesByGroupId(groupId, paginationRequest);

    if (message.isEmpty()) return null;

    return message.get(message.size() - 1).getRight();
  }

  @Override
  public void saveOrChangeReactionMessage(String msgId, Long userId, EReactionType reactionType) {
    MessageCollection messageCollection = messageRepository.findById(msgId).orElseThrow();
    final EReactionTypeEntity reactionTypeEntity = reactionTypeMapperETD.toEntity(reactionType);

    List<ReactionMessagesCollection> reactions = messageCollection.getReactionMsgs();
    if (reactions == null) {
      reactions = new ArrayList<>();
      messageCollection.setReactionMsgs(reactions);
    }

    final List<ReactionMessagesCollection> finalReactions = reactions;

    boolean[] reactionRemoved = {false};

    finalReactions.stream()
            .filter(r -> r.getUserId().equals(userId))
            .findFirst()
            .ifPresentOrElse(
                    existingReaction -> {
                      if (existingReaction.getReactionType().equals(reactionTypeEntity)) {
                        finalReactions.remove(existingReaction);
                        reactionRemoved[0] = true;
                      } else {
                        existingReaction.setReactionType(reactionTypeEntity);
                      }
                    },
                    () -> finalReactions.add(new ReactionMessagesCollection(reactionTypeEntity, userId))
            );

    long reactionCount = finalReactions.size();
    messageCollection.setReactionQuantity(reactionCount);

    if (reactionCount == 0) {
      messageCollection.setReactionMsgs(null);
    }

    messageRepository.save(messageCollection);
  }

  @Override
  public List<Pair<Message, Message>> getMessagesByGroupId(String groupId, PaginationRequest paginationRequest) {
    Pageable pageable = paginationRequest.toPageable();
    return messageRepository.findAllByGroupId(groupId, pageable).stream().map(
            m -> {
              Message messageParent = null;
              if (m.getReplyMsgId() != null) {
                messageParent = getMessageById(m.getReplyMsgId());
              }
              return Pair.of(
                      messageParent,
                      messageMapperETD.messageCollectionToMessage(m)
              );
            }
    ).toList();
  }
}
