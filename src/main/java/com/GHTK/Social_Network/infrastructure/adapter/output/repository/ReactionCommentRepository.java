package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionTypeEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ReactionCommentEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReactionCommentRepository extends JpaRepository<ReactionCommentEntity, Long> {
  @Query("""
                 select r from ReactionCommentEntity r where r.userEntity.userId = ?2 and r.commentEntity.commentId = ?1
          """)
  ReactionCommentEntity findByCommentIdAndUserID(Long postId, Long userId);

  @Query("""
                 select r from ReactionCommentEntity r where r.commentEntity.commentId = ?1 and r.userEntity.userId not in ?2
          """)
  List<ReactionCommentEntity> findByCommentId(Long postId, List<Long> blockIds);

  @Query("""
                 select count(r) from ReactionCommentEntity r where r.commentEntity.commentId = ?1
          """)
  int countReactionByCommentId(Long postId);

  @Query("""
                  select count(r) from ReactionCommentEntity r where r.commentEntity.commentId = ?1 and r.reactionType = ?2
            """)
  int countReactionByCommentIdAndType(Long postId, EReactionTypeEntity reactionType);

  @Query(value = """
  SELECT r.reaction_type AS reactionType, 
         JSON_ARRAYAGG(JSON_OBJECT(
             'user_id', r.user_id, 
             'comment_id', r.comment_id, 
             'reaction_comment_id', r.reaction_comment_id, 
             'create_at', r.create_at
         )) AS reaction_comments
  FROM reaction_comment r
  WHERE r.comment_id = :commentId
  GROUP BY r.reaction_type
  """, nativeQuery = true)
  List<Map<String, Object>> getReactionGroupByCommentId(@Param("commentId") Long commentId);

  @Query("""
                 select r from ReactionCommentEntity r where r.commentEntity.commentId = ?1
          """)
  List<ReactionCommentEntity> getByCommentId(Long postId, Pageable pageable);

  @Query("""
                 select r from ReactionCommentEntity r where r.commentEntity.commentId = ?1 and r.reactionType = ?2
          """)
  List<ReactionCommentEntity> getByCommentIdAndType(Long postId, EReactionTypeEntity reactionType, Pageable pageable);

  @Query("""
                 select r from ReactionCommentEntity r where ( r.commentEntity.commentId = ?1 and r.userEntity.userId not in ?2 )
          """)
  List<ReactionCommentEntity> getByCommentId(Long postId,  List<Long> listBlock, Pageable pageable);

  @Query("""
                 select r from ReactionCommentEntity r where ( r.commentEntity.commentId = ?1 and r.reactionType = ?2 and r.userEntity.userId not in ?3 )
          """)
  List<ReactionCommentEntity> getByCommentIdAndType(Long postId, EReactionTypeEntity reactionType, List<Long> listBlock,  Pageable pageable);

}