package com.GHTK.Social_Network.infrastructure.adapter.output.repository.node;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.friendShip.EFriendshipStatusEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.*;
import org.neo4j.driver.internal.value.FloatValue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserNodeRepository extends Neo4jRepository<UserNode, Long> {

    @Query("MATCH (user:User {userId: $userId})\n" +
            "MATCH (potentialFriend:User)\n" +
            "WHERE user <> potentialFriend\n" +
            "  AND NOT (user)-[:BLOCK_TO]->(potentialFriend)\n" +
            "  AND NOT (potentialFriend)-[:BLOCK_TO]->(user)\n" +
            "  AND NOT (user)-[:RELATED_TO]-(potentialFriend)\n" +
            "  AND NOT potentialFriend.userId IN $excludeUserIds\n" +
            "OPTIONAL MATCH (user)-[r1:RELATED_TO]-(mutualFriend:User)-[r2:RELATED_TO]-(potentialFriend)\n" +
            "WITH user, potentialFriend, COUNT(DISTINCT mutualFriend) AS mutualFriendsCount,\n" +
            "     SUM(CASE r1.relationship\n" +
            "         WHEN 'CLOSE_FRIEND' THEN $closeFriendScore\n" +
            "         WHEN 'SIBLING' THEN $siblingScore\n" +
            "         WHEN 'PARENT' THEN $parentScore\n" +
            "         WHEN 'OTHER' THEN $otherScore\n" +
            "         ELSE 0 END +\n" +
            "         CASE r2.relationship\n" +
            "         WHEN 'CLOSE_FRIEND' THEN $closeFriendScore\n" +
            "         WHEN 'SIBLING' THEN $siblingScore\n" +
            "         WHEN 'PARENT' THEN $parentScore\n" +
            "         WHEN 'OTHER' THEN $otherScore\n" +
            "         ELSE 0 END) AS relationshipScore\n" +
            "OPTIONAL MATCH (user)-[:LIVES_IN]->(userHometown:Hometown)\n" +
            "OPTIONAL MATCH (potentialFriend)-[:LIVES_IN]->(friendHometown:Hometown)\n" +
            "WITH user, potentialFriend, mutualFriendsCount, relationshipScore,\n" +
            "     CASE WHEN userHometown = friendHometown THEN $sameHometownScore ELSE 0 END AS hometownScore\n" +
            "WITH user, potentialFriend, \n" +
            "     mutualFriendsCount + relationshipScore + hometownScore AS totalScore\n" +
            "RETURN potentialFriend, totalScore\n" +
            "ORDER BY totalScore DESC\n" +
            "LIMIT 10")
    List<FriendSuggestion> getListPotentialFriends(
            Long userId,
            List<Long> excludeUserIds,
            int closeFriendScore,
            int siblingScore,
            int parentScore,
            int otherScore,
            int sameHometownScore
    );


    @Query("""
        CALL db.index.fulltext.queryNodes('userIndex', "*" + $searchTerm + "*") YIELD node, score
        MATCH (currentUser:User {userId: $currentUserId})
        OPTIONAL MATCH (node)-[:RELATED_TO]-(relatedUser:User)-[:RELATED_TO]-(currentUser)
        OPTIONAL MATCH (node)-[relationship:RELATED_TO]-(currentUser)
        OPTIONAL MATCH (node)-[:LIVES_IN]->(hometown:Hometown)
        OPTIONAL MATCH (currentUser)-[:LIVES_IN]->(currentUserHometown:Hometown)
        WITH node, score,
             CASE
                 WHEN (relatedUser IS NOT NULL  OR relationship IS NOT NULL) THEN 1
                 ELSE 0
             END AS hasRelationship,
             CASE
                 WHEN hometown = currentUserHometown THEN 1
                 ELSE 0
             END AS sameHometown,
             CASE
                 WHEN node = currentUser THEN 20
                 ELSE 1
             END AS isMe
        WHERE NOT EXISTS {MATCH (currentUser)-[:BLOCK_TO]-(node)}
        RETURN node.userId AS userId
        ORDER BY score + (1 * hasRelationship) + (0.1 * isMe) + (0.1 * sameHometown) DESC
        SKIP $skip
        LIMIT $limit
    """)
    List<Long> searchUserFullTextSearch(String searchTerm, Long currentUserId, Pageable pageable);

    @Query("""
        CALL db.index.fulltext.queryNodes('userIndex', CASE WHEN $searchTerm IS NOT NULL AND $searchTerm <> '' THEN "*" + $searchTerm + "*" ELSE "*" END) YIELD node AS friend, score
        MATCH (friend:User)-[:RELATED_TO]-(user:User {userId: $userId})
        RETURN friend.userId AS friendId
        ORDER BY score DESC
        SKIP $skip
        LIMIT $limit
        """)
    List<Long> searchFriendFullTextSearch(String searchTerm, Long userId, Pageable pageable);

    @Query("MATCH (u:User {email: $email})\n" +
            "RETURN u")
    UserNode getUserNodeByEmail(String email);

    @Query("MATCH (u:User {userId: $userId})\n" +
            "RETURN u")
    UserNode getUserNodeById(Long userId);

    @Query("MATCH (u:User {userId: $userId})-[r:RELATED_TO]->(p:Post {postId: $postId}) " +
            "RETURN r")
    UserPostRelationship findExistingUserPostRelationship(Long userId, Long postId);

    @Query("MATCH (u1:User {userId: $userId1})\n" +
            "MATCH (u2:User {userId: $userId2})\n" +
            "MERGE (u1)-[r:RELATED_TO]-(u2)\n" +
            "ON CREATE SET r.relationship = $relationship\n" +
            "ON MATCH SET r.relationship = $relationship\n" +
            "RETURN r")
    UserRelationship createOrUpdateFriend(Long userId1, Long userId2, EFriendshipStatusEntity relationship);

    @Query("MATCH (u:User {userId: $userId}) " +
            "MATCH (p:Post {postId: $postId}) " +
            "MERGE (u)-[r:RELATED_TO_POST]->(p) " +
            "SET r.score = $score " +
            "SET r.updatedAt = $updatedAt " +
            "SET u.maxScorePost = $score ")
    void createOrUpdateUserPostRelationship(Long userId, Long postId, Integer score, LocalDateTime updatedAt);

    @Query("MATCH (u1:User {userId: $firstUser})\n" +
            "MATCH (u2:User {userId: $secondUser})\n" +
            "MATCH (u1)-[f:RELATED_TO]-(u2) " +
            "DELETE f")
    void deleteFriend(Long firstUser, Long secondUser);

    @Query("MATCH (initiator:User {userId: $initiator})\n" +
            "MATCH (receiver:User {userId: $receiver})\n" +
            "OPTIONAL MATCH (initiator)-[f:RELATED_TO]-(receiver) " +
            "DELETE f " +
            "MERGE (initiator)-[:BLOCK_TO]->(receiver)")
    void createBlockUser(Long initiator, Long receiver);

    @Query("MATCH (initiator:User {userId: $initiator})\n" +
            "MATCH (receiver:User {userId: $receiver})\n" +
            "MATCH (initiator)-[r:BLOCK_TO]->(receiver)\n" +
            "DELETE r")
    void unblockUser(Long initiator, Long receiver);


    @Query("MATCH (u:User {userId: $userId}) " +
            "MATCH (u)-[:RELATED_TO]-(f:User) " +
            "RETURN f")
    List<UserNode> getFriends(Long userId);

    @Query("MATCH (u:User {userId: $userId}) " +
            "MATCH (u)-[:BLOCK_TO]->(b:User) " +
            "RETURN b")
    List<UserNode> getBlockedUsers(Long userId);

    @Query("MATCH (u:User {userId: $userId})" +
            "RETURN u")
    Optional<UserNode> findByUserId(Long userId);

    @Query("MERGE (hometown:Hometown {hometownId: $hometownId})\n" +
            "RETURN hometown")
    HometownNode createOrGetHometown(Integer hometownId);

    @Query("MATCH (h:Hometown {hometownId: $hometownId}) RETURN h.hometownId AS hometownId")
    HometownNode getHometownByHometownId(Integer hometownId);

    @Query("MATCH (u:User {userId: $userId})-[:LIVES_IN]-(h:Hometown) " +
            "RETURN h.hometownId AS hometownId " +
            "LIMIT 1")
    HometownNode getUserWithHometown(Long userId);

    @Query("MATCH (u:User {userId: $userId})-[r:LIVES_IN]->(hometown:Hometown)\n" +
            "DELETE r")
    void removeUserHometown(Long userId);

    @Query("MATCH (u:User {userId: $userId})\n" +
            "MERGE (hometown:Hometown {hometownId: $hometownId})\n" +
            "MERGE (u)-[:LIVES_IN]->(hometown)")
    void setUserHometown(Long userId, Integer hometownId);


    @Query("""
            MATCH (user1:User {userId: $firstUser})-[:RELATED_TO]-(mutualFriend:User)-[:RELATED_TO]-(user2:User {userId: $secondUser})
            RETURN COUNT(DISTINCT mutualFriend) AS mutualFriendsCount
            """)
    int getMutualFriend(Long firstUser, Long secondUser);


    @Query("""
                MATCH (u:User {userId: $userId})
                MATCH (p:Post {id: $postId})
                MERGE (u)-[r:RELATED_TO]->(p)
                SET r.score = $score
                SET u.maxScorePost = $score
                RETURN u
            """)
    UserNode setMaxScorePost(Long userId, Long postId, Integer score);

}
