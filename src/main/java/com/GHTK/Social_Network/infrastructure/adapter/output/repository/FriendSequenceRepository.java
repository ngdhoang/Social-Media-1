package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.FriendshipCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface FriendSequenceRepository extends MongoRepository<FriendshipCollection, String> {
    @Query("{ 'userId' : ?0 }")
    FriendshipCollection findByUserId(Long userId);


    @Query(
            value = "{ 'userId' : ?0 }",
            fields = "{ 'listFriendId' : 1 }"
    )
    void addFriend(Long userId, Long friendId);

    @Query(
            value = "{ 'userId' : ?0 }",
            fields = "{ 'listFriendId' : 1 }"
    )
    void removeFriend(Long userId, Long friendId);

    @Query(
            value = "{ 'userId' : ?0 }",
            fields = "{ 'listBlockId' : 1 }"
    )
    void blockFriend(Long userId, Long friendId);

    @Query(
            value = "{ 'userId' : ?0 }",
            fields = "{ 'listBlockId' : 1 }"
    )
    void unblockFriend(Long userId, Long friendId);


}
