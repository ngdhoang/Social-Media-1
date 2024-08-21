package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUserEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
  @Query("""

          select p from PostEntity p where p.userEntity.userId = ?1 and p.userEntity.userId not in ?2
          """)
  List<PostEntity> getListByUserId(Long userEntityId, List<Long> blockIds, Pageable pageable);

  @Query("""
              select p from PostEntity p join p.tagUserEntities t where t = ?1
          """)
  PostEntity findByTagUsers(TagUserEntity tagUserEntity);

  @Query("""
              select p from PostEntity p join p.imagePostEntities i where i.imagePostId = ?1
          """)
  PostEntity findByImagePostId(Long imagePostEntityId);


  @Query(value = """
      select p.*
      from post p
      join user u on p.user_id = u.user_id
      where u.user_id = :userId
      and (
        (:status = 'PRIVATE' and p.post_status = 'PRIVATE')
        or (:status = 'PUBLIC' and p.post_status = 'PUBLIC')
        or (:status = 'FRIEND' and p.post_status in ('FRIEND', 'PUBLIC'))
        or (:status = 'ALL' and p.post_status in ('FRIEND', 'PUBLIC', 'PRIVATE'))
      )
      """, nativeQuery = true)
  List<PostEntity> getListByUserIdAndFriendStatus(@Param("userId") Long userId, @Param("status") String status, Pageable pageable);

  @Query(value = """
          select distinct p from PostEntity p
          left join p.userEntity
          left join p.imagePostEntities
          left join p.tagUserEntities
          where p.postStatus = 'PUBLIC'
          and (
            exists (select 1 from CommentEntity c where c.postEntity = p and c.userEntity.userId = :userId)
            or
            exists (select 1 from ReactionPostEntity r where r.postEntity = p and r.userEntity.userId = :userId and r.commentEntity is null)
          )

          order by p.createAt desc
          """, nativeQuery = true)
  List<PostEntity> findPostsWithUserInteractions(@Param("userId") Long userId);

  @Modifying
  @Transactional
  @Query("""
              update PostEntity p set p.reactionsQuantity = p.reactionsQuantity + 1 where p.postId = :postId
          """)
  void increaseReactionsQuantity(Long postId);

  @Modifying
  @Transactional
  @Query("""
        update PostEntity p set p.commentQuantity = p.commentQuantity + 1 where p.postId = :postId
    """)
  int increaseCommentQuantity(Long postId);

  @Modifying
  @Transactional
  @Query("""
              update PostEntity p set p.commentQuantity = p.commentQuantity - :numberOfComments where p.postId = :postId
          """)
  void decreaseCommentQuantity(Long postId, Long numberOfComments);

  @Modifying
  @Transactional
  @Query("""
              update PostEntity p set p.reactionsQuantity = p.reactionsQuantity - 1 where p.postId = :postId
          """)
  void decreaseReactionsQuantity(Long postId);

}
