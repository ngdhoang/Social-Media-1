package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.FriendshipCollection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.Optional;


@Repository
public interface FriendCollectionRepository extends MongoRepository<FriendshipCollection, String> {
//    @Query("{ 'userId' : ?0 }")
//    FriendshipCollection findByUserId(String userId);

    @Query(
            """
                    {
                        $or: [
                            {
                                $and: [
                                    { 'userId' : ?0 },
                                    { 'listFriendId' : ?1 }
                                ]
                            },
                            {
                                $and: [
                                    { 'userId' : ?1 },
                                    { 'listFriendId' : ?0 }
                                ]
                            }
                        ]
                    }
            """
    )
    FriendshipCollection getBlock(Long firstUserId, Long secondUserId);
    FriendshipCollection findByUserId(Long userId);

    @Query(value = "{ 'userId' : ?0 }", fields = "{ 'listFriendId' : 1}")
    LinkedList<Long>  getFriendsById(Long userId);

    @Query(value = "{ 'userId' : ?0 }", fields = "{ 'listBlockId' : 1}")
    LinkedList<Long> getBlocksById(Long userId);

    @Query(value = "{ 'userId' : ?0 }", fields = "{ 'listBlockedId' : 1}")
    LinkedList<Long>  getBlockedsById(Long userId);


    boolean existsByUserIdAndListFriendIdContains(Long userId, Long friendId);

    boolean existsByUserIdAndListBlockIdContains(Long userId, Long blockId);

    boolean existsByUserIdAndListBlockedIdContains(Long userId, Long blockedId);


}
