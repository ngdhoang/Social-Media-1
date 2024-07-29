package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.FriendshipCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface FriendCollectionRepository extends MongoRepository<FriendshipCollection, String> {
    @Query("{ 'userId' : ?0 }")
    FriendshipCollection findByUserId(String userId);

}
