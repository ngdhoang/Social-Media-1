package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EReactionTypeEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ReactionPostEntity;
import io.lettuce.core.dynamic.annotation.Param;
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

  @Query(value = """
          SELECT r.reaction_type AS reactionType, 
                 JSON_ARRAYAGG(JSON_OBJECT(
                     'user_id', r.user_id, 
                     'post_id', r.post_id, 
                     'reaction_post_id', r.reaction_post_id, 
                     'create_at', r.create_at
                 )) AS reaction_posts
          FROM reaction_post r
          WHERE r.post_id = :postId
          GROUP BY r.reaction_type
          """, nativeQuery = true)
  List<Map<String, Object>> getReactionGroupByPostId(@Param("postId") Long postId);

  @Query("""
                 select r from ReactionPostEntity r where r.postEntity.postId = ?1
          """)
  List<ReactionPostEntity> getByPostId(Long postId, Pageable pageable);

  @Query("""
                 select r from ReactionPostEntity r where r.postEntity.postId = ?1 and r.reactionType = ?2
          """)
  List<ReactionPostEntity> getByPostIdAndType(Long postId, EReactionTypeEntity reactionType, Pageable pageable);

  @Query("""
                 select r from ReactionPostEntity r where ( r.postEntity.postId = ?1 and r.userEntity.userId not in ?2 ) 
          """)
  List<ReactionPostEntity> getByPostId(Long postId, List<Long> listBlock, Pageable pageable);

  @Query("""
                 select r from ReactionPostEntity r where ( r.postEntity.postId = ?1 and r.reactionType = ?2 and r.userEntity.userId not in ?3 )
          """)
  List<ReactionPostEntity> getByPostIdAndType(Long postId, EReactionTypeEntity reactionType, List<Long> listBlock, Pageable pageable);

  @Query(value =
          """
                      SELECT r.reaction_post_id AS roleId, r.reaction_type AS reactionType, r.post_id AS postId, DATE(r.create_at) AS createAt, "post" as role
                      FROM reaction_post r
                      WHERE r.user_id = :userId 
                      UNION ALL
                      SELECT r.reaction_comment_id AS roleId, r.reaction_type AS reactionType, r.comment_id AS commentId, DATE(r.create_at) AS createAt, "comment" as role
                      FROM reaction_comment r
                      WHERE r.user_id = :userId
                  """, nativeQuery = true)
  List<Object[]> getReactionPostAndCommentByUserId(@Param("userId") Long userId, Pageable pageable);
}