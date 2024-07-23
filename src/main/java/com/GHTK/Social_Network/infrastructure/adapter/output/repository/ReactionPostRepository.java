package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionTypeEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionPostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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

  @Query("""
                 select count(r) from ReactionPostEntity r where r.postEntity.postId = ?1
          """)
  int countReactionByPostId(Long postId);

  @Query("""
                  select count(r) from ReactionPostEntity r where r.postEntity.postId = ?1 and r.reactionType = ?2
            """)
  int countReactionByPostIdAndType(Long postId, EReactionTypeEntity reactionType);

  @Query(value = "SELECT r.reactionType AS reactionType, " +
          "       JSON_ARRAYAGG(JSON_OBJECT('userId', r.userId, 'postId', r.postId, 'reactionPostId', r.reactionPostId, 'createdAt', r.createdAt)) AS reaction_posts " +
          "FROM ReactionPostEntity r " +
          "WHERE r.postId = ?1 " +
          "GROUP BY r.reactionType", nativeQuery = true)
  Map<String, Object> getReactionGroupByPostId(Long postId);

  @Query("""
                 select r from ReactionPostEntity r where r.postEntity.postId = ?1
          """)
  List<ReactionPostEntity> getByPostId(Long postId, Pageable pageable);

  @Query("""
                 select r from ReactionPostEntity r where r.postEntity.postId = ?1 and r.reactionType = ?2
          """)
  List<ReactionPostEntity> getByPostIdAndType(Long postId, EReactionTypeEntity reactionType, Pageable pageable);

}