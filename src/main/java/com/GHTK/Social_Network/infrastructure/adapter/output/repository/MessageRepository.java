package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.MessageCollection;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat.ReactionMessagesCollection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<MessageCollection, String> {
    List<MessageCollection> findAllByGroupId(String groupId, Pageable pageable);
}

