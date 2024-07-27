package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionTypeEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface ReactionPostRepository extends JpaRepository<ReactionEntity, Long> {
  @Query("""
                 select r from ReactionEntity r where r.userEntity.userId = ?2 and r.postEntity.postId = ?1
          """)
  ReactionEntity findByPostIdAndUserID(Long postId, Long userId);

  @Query("""
                 select r from ReactionEntity r where r.postEntity.postId = ?1
          """)
  List<ReactionEntity> findByPostId(Long postId);

  @Query("""
                 select count(r) from ReactionEntity r where r.postEntity.postId = ?1
          """)
  int countReactionByPostId(Long postId);

  @Query("""
                select count(r) from ReactionEntity r where r.postEntity.postId = ?1 and r.reactionType = ?2
          """)
  int countReactionByPostIdAndType(Long postId, EReactionTypeEntity reactionType);

  @Query(value = """
          SELECT r.reaction_type AS reactionType, 
                 JSON_ARRAYAGG(JSON_OBJECT(
                     'user_id', r.user_id, 
                     'post_id', r.post_id, 
                     'reaction_post_id', r.reaction__id, 
                     'create_at', r.create_at
                 )) AS reaction_posts
          FROM reaction r
          WHERE r.post_id = :postId
          GROUP BY r.reaction_type
          """, nativeQuery = true)
  List<Map<String, Object>> getReactionGroupByPostId(@Param("postId") Long postId);

  @Query("""
                 select r from ReactionEntity r where r.postEntity.postId = ?1
          """)
  List<ReactionEntity> getByPostId(Long postId, Pageable pageable);

  @Query("""
                 select r from ReactionEntity r where r.postEntity.postId = ?1 and r.reactionType = ?2
          """)
  List<ReactionEntity> getByPostIdAndType(Long postId, EReactionTypeEntity reactionType, Pageable pageable);

  @Query(
          """
                          SELECT r from ReactionEntity r
                          WHERE r.postEntity.postId = ?1
                          AND r.commentEntity.userEntity.userId = ?2
                  """
  )
  ReactionEntity findByCommentIdAndUserId(Long userId, Long commentId);


//  ReactionEntity saveReactionComment(ReactionPost reactionComment);


  @Query(
          """
                              select r from ReactionEntity r
                              where r.userEntity.userId = ?2\s
                              and r.commentEntity.commentId = ?1
                  """
  )
  ReactionEntity findReactionCommentByCommentIdAndUserId(Long commentId, Long userId);
}