package com.GHTK.Social_Network.infrastructure.repository;

import com.GHTK.Social_Network.infrastructure.entity.postEntity.ReactionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactionPostRepository extends JpaRepository<ReactionPost, Long> {
  @Query("""
                 select r from ReactionPost r where r.userEntity.userId = ?2 and r.postEntity.postId = ?1
          """)
  ReactionPost findByPostIdAndUserID(Long postId, Long userId);

  @Query("""
                 select r from ReactionPost r where r.postEntity.postId = ?1
          """)
  List<ReactionPost> findByPostId(Long postId);
}
