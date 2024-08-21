package com.GHTK.Social_Network.infrastructure.adapter.output.repository.node;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.CommentNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.PostNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CommentNodeRepository extends Neo4jRepository<CommentNode, Long> {

    @Query("MATCH (c:Comment{commentId: $commentId}) RETURN c")
    CommentNode getCommentNodeByCommentId(Long commentId);

    @Query("MATCH (c:Comment{commentId: $commentId}) DETACH DELETE c")
    void deleteCommentNodeByCommentId(Long commentId);

    @Query("MATCH (u:User{userId: $userId}) " +
            "MATCH (p:Post{postId: $postId}) " +
            "CREATE (c:Comment{commentId: $commentId, createAt: $createAt}) " +
            "CREATE (u)-[:COMMENTED]->(c) " +
            "CREATE (c)-[:COMMENT_TO]->(p) " +
            "RETURN c")
    CommentNode createCommentNodeByPostIdAndUserId(Long commentId, Long postId, Long userId, LocalDateTime createAt);


    @Query("MATCH (u:User{userId: $userId}) " +
            "MATCH (p:Post{postId: $postId}) " +
            "MATCH (c:Comment{commentId: $commentId})" +
            "CREATE (u)-[:COMMENTED]->(c) " +
            "CREATE (c)-[:COMMENT_TO]->(p) " +
            "RETURN c")
    PostNode createCommentNodeByUserIdAndPostId(Long commentId, Long postId, Long userId);

    @Query("MATCH (p:Post{postId: $postId}) SET p.postStatus = $postStatus RETURN p")
    PostNode updatePostNodeByPostId(Long postId, EPostStatusEntity postStatus);
}
