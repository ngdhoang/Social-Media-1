package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.domain.entity.post.ReactionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactionPostRepository extends JpaRepository<ReactionPost, Long> {
  @Query("""
                 select r from ReactionPost r where r.user.userId = ?2 and r.post.postId = ?1
          """)
  ReactionPost findByPostIdAndUserID(Long postId, Long userId);

  @Query("""
                 select r from ReactionPost r where r.post.postId = ?1
          """)
  List<ReactionPost> findByPostId(Long postId);
}
