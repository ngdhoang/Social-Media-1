package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.application.port.output.chat.CustomMessageRepository;
import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<MessageCollection, String> {
  List<MessageCollection> findByReactionMsgsUserId(Long userId);

  @Query(value = "{ 'reactionMsgs.userId' : ?0 }", fields = "{ 'reactionMsgs.$' : 1 }")
  List<MessageCollection> findReactionsByUserId(Long userId);

  @Query(value = "{}", fields = "{'reactionMsgs': 0}")
  List<MessageCollection> findAllWithoutReactions();

  @Query(value = "{'msgId': ?0}", fields = "{'reactionMsgs': 0}")
  Optional<MessageCollection> findByIdWithoutReactions(String msgId);

  @Query(value = "{'msgId': ?0, 'reactionMsgs.userId': ?1}", fields = "{'reactionMsgs': 1}")
  Optional<MessageCollection> findReactionsById(String msgId, Long userId);
}
