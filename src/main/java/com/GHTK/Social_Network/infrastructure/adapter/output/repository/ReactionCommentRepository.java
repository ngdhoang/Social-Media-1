package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ReactionCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionCommentRepository extends JpaRepository<ReactionCommentEntity, Long> {
  @Query("""
                  select r from ReactionCommentEntity r
                  where r.commentEntity.commentId = ?2
                  and r.userEntity.userId = ?1
          """)
  ReactionCommentEntity findByCommentIdAndUserId(Long userId, Long commentId);

  @Query("""
              select r from ReactionCommentEntity r 
              where r.commentEntity.commentId = ?2 
              and r.userEntity.userId = ?1
          """)
  ReactionCommentEntity findByUserIdAndCommentId(Long userId, Long commentId);
}
