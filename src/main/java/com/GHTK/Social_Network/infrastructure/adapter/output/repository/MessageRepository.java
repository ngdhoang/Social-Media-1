package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.ReactionMessagesCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<MessageCollection, String> {
    @Query(value = "{'msgId': ?0, 'reactionMsgs.userId': ?1}", fields = "{'reactionMsgs': 1}")
    Optional<MessageCollection> findReactionsById(String msgId, Long userId);

    @Query("{'_id': ?0, 'reactionMsgs.userId': ?1}")
    @Update("{'$set': {'reactionMsgs.$': ?2}, '$set': {'content': ?3}}")
    void updateReaction(String msgId, Long userId, ReactionMessagesCollection reaction, String content);

    @Query("{'_id': ?0}")
    @Update("{'$push': {'reactionMsgs': ?1}, '$set': {'content': ?2}}")
    void addReaction(String msgId, ReactionMessagesCollection reaction, String content);
}

