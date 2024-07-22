package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.commentEntity.ReactionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionCommentRepository extends JpaRepository<ReactionComment, Long> {
  @Query("""
                  select r from ReactionCommentEntity r 
                  where r.commentEntity.commentId = ?2 
                  and r.userEntity.userId = ?1
          """)
  ReactionComment findByCommentIdAndUserId(Long userId, Long commentId);
}
