package com.GHTK.Social_Network.infrastructure.adapter.output.persistence.customRepository;

import com.GHTK.Social_Network.application.port.output.chat.CustomMessageRepository;
import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.ReactionMessagesCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionTypeEntity;
import com.GHTK.Social_Network.infrastructure.mapper.ReactionTypeMapperETD;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomMessageRepositoryAdapter implements CustomMessageRepository {
  private final ReactionTypeMapperETD reactionTypeMapperETD;

  private final MongoTemplate mongoTemplate;

  @Override
  public void updateOrAddReactionMessage(String msgId, Long userId, EReactionType reactionType) {
    EReactionTypeEntity reactionTypeEntity = reactionTypeMapperETD.toEntity(reactionType);

    Query query = new Query(Criteria.where("msgId").is(msgId)
            .and("reactionMsgs.userId").is(userId));

    Update update = new Update().set("reactionMsgs.$.reactionType", reactionTypeEntity);

    try {
      UpdateResult result = mongoTemplate.updateFirst(query, update, MessageCollection.class);

      if (result.getMatchedCount() == 0) {
        Query msgQuery = new Query(Criteria.where("msgId").is(msgId));
        Update addReactionUpdate = new Update().push("reactionMsgs", new ReactionMessagesCollection(reactionTypeEntity, userId));

        UpdateResult addResult = mongoTemplate.updateFirst(msgQuery, addReactionUpdate, MessageCollection.class);

        if (addResult.getMatchedCount() == 0) {
          throw new CustomException("Message not found", HttpStatus.NOT_FOUND);
        }
      }
    } catch (Exception e) {
      throw new CustomException("Update error: " + e.getMessage(), HttpStatus.CONFLICT);
    }
  }

  @Override
  public void deleteReactionMessageCustom(String msgId, Long userId) {
    Query query = new Query(Criteria.where("msgId").is(msgId));
    Update update = new Update().pull("reactionMsgs", new Query(Criteria.where("userId").is(userId)));

    mongoTemplate.updateFirst(query, update, MessageCollection.class);
  }
}
