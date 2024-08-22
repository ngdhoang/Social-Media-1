package com.GHTK.Social_Network.infrastructure.adapter.output.repository.node;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.PostNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.UserPostRelationship;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostNodeRepository extends Neo4jRepository<PostNode, Long> {

    @Query("MATCH (postNode:Post{postId: $postId}) RETURN postNode LIMIT 1")
    PostNode getPostNodeByPostId(Long postId);

    @Query("MATCH (postNode:Post{postId: $postId}) DETACH DELETE postNode")
    void deletePostNodeBy(Long postId);

    @Query("MATCH (u:User{userId: $userId}) " +
            "CREATE (p:Post{postId: $postId, postStatus: $postStatus, createAt: $createAt}) " +
            "CREATE (u)-[:POSTED]->(p) " +
            "RETURN p")
    PostNode createPostNodeByUserIdAndPostId(Long postId, Long userId, EPostStatusEntity postStatus, LocalDateTime createAt);

    @Query("MATCH (u:User{userId: $userId}) " +
            "MATCH (p:Post{postId: $postId}) " +
            "CREATE (u)-[:POSTED]->(p) " +
            "RETURN p")
    PostNode createPostNodeByUserIdAndPostId(Long postId, Long userId);

    @Query("MATCH (p:Post{postId: $postId}) SET p.postStatus = $postStatus RETURN p")
    PostNode updatePostNodeByPostId(Long postId, EPostStatusEntity postStatus);

    @Query("MATCH (p:Post{postId: $postId}) SET p.lastCommentAt = $lastCommentAt RETURN p")
    PostNode updateLastCommentAtByPostId(Long postId, LocalDateTime lastCommentAt);

    @Query("MATCH (u:User)-[r:RELATED_TO_POST]-(p:Post{postId: $postId}) RETURN r")
    List<UserPostRelationship> getUserPostRelationshipByPostId(Long postId);

    @Query("MATCH (u:User{userId: $userId})-[r:RELATED_TO_POST]-(p:Post{postId: $postId}) RETURN r")
    UserPostRelationship getUserRelationshipByUserIdAndPostId(Long userId, Long postId);

    @Query("""
            MATCH (user:User {userId: $userId})
            CALL {
                WITH user
                MATCH (user)-[:RELATED_TO]->(friend:User)-[:POSTED]->(post:Post)
                WHERE date(post.createAt) = date($date)
                AND ( post.postStatus = 'PUBLIC' OR post.postStatus = 'FRIEND' )
                MATCH (user)-[rel:RELATED_TO_POST]->(post)
                RETURN post, rel, 1 AS priority
                
                UNION ALL
                
                WITH user
                MATCH (otherUser:User)-[:POSTED]->(post:Post)
                WHERE NOT (user)-[:BLOCK_TO]-(otherUser) 
                AND NOT (user)-[:RELATED_TO]-(otherUser)
                AND post.postStatus = 'PUBLIC'
                AND date(post.createAt) = date($date)               
                MATCH (user)-[rel:RELATED_TO_POST]->(post)
                RETURN post, rel, 2 AS priority
                
                UNION ALL
                
                WITH user
                MATCH (otherUser:User)-[:POSTED]->(post:Post)
                WHERE date(post.createAt) < date($date)
                AND (
                        (
                            (user)-[:RELATED_TO]-(otherUser)
                            AND (post.postStatus = 'FRIEND' OR post.postStatus = 'PUBLIC')
                        )
                        OR
                        (
                            post.postStatus = 'PUBLIC'
                            AND NOT (user)-[:BLOCK_TO]-(otherUser)
                        )
                )
                OPTIONAL MATCH (user)-[rel:RELATED_TO_POST]->(post)
                RETURN post, rel, 3 AS priority
            }
            WITH post, rel, priority
            ORDER BY
                priority,
                CASE
                    WHEN priority < 3 THEN rel.score
                    ELSE null
                END DESC,
                post.createAt DESC
            RETURN post.postId
            SKIP $skip LIMIT $limit
            """)
    List<Long> getSuggestedPostsWithPagination(Long userId, LocalDate date, Pageable pageable);


    @Query("MATCH (p:Post{postId: $postId}) SET p.postStatus = $postStatus RETURN p")
    PostNode updateStatus(Long postId, EPostStatusEntity postStatus);
}
