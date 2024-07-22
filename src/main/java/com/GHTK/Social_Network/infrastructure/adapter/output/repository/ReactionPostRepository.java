package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.postEntity.ReactionPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactionPostRepository extends JpaRepository<ReactionPostEntity, Long> {
  @Query("""
                 select r from ReactionPostEntity r where r.userEntity.userId = ?2 and r.postEntity.postId = ?1
          """)
  ReactionPostEntity findByPostIdAndUserID(Long postId, Long userId);

  @Query("""
                 select r from ReactionPostEntity r where r.postEntity.postId = ?1
          """)
  List<ReactionPostEntity> findByPostId(Long postId);
}
