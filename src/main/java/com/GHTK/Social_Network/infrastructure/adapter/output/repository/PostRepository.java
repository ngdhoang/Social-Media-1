package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
  @Query("""
          select p from PostEntity p where p.userEntity.userId = ?1
          """)
  List<PostEntity> findAllByUserId(Long userEntityId);

  @Query("""
              select p from PostEntity p join p.tagUserEntities t where t = ?1
          """)
  PostEntity findByTagUsers(TagUserEntity tagUserEntity);

  @Query("""
              select p from PostEntity p join p.imagePostEntities i where i.imagePostId = ?1
          """)
  PostEntity findByImagePostId(Long imagePostEntityId);


  @Query("""
              select p from PostEntity p
              where p.userEntity.userId = :userId
              and (
                  p.postStatus = :status
                  or (:status = 'PRIVATE' and p.postStatus in (com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.PRIVATE))
                  or (:status = 'PUBLIC' and p.postStatus in (com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.PUBLIC))
                  or (:status = 'FRIEND' and p.postStatus in (com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.FRIEND, com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.PUBLIC))
                  or (:status = 'ALL' and p.postStatus in (com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.FRIEND, com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.PUBLIC, com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.PRIVATE))
              )
          """)
  List<PostEntity> findAllByUserIdAndFriendStatus(@Param("userId") Long userId, @Param("status") String status);


    @Query(value = """
                select distinct * from PostEntity p
                where p.postStatus = 'PUBLIC'
                and (
                    exists (select c from CommentEntity c where c.postEntity = p and c.userEntity.userId = :userId)
                    or
                    exists (select r from ReactionPostEntity r where r.postEntity = p and r.userEntity.userId = :userId)
                )
                order by p.createdAt desc
            """, nativeQuery = true)
    List<PostEntity> findPostsWithUserInteractions(@Param("userId") Long userId);

    @Query("""
            update PostEntity p set p.reactionsQuantity = p.reactionsQuantity + 1 where p.postId = :postId
        """)
    void increaseReactionsQuantity(Long postId);

    @Query("""
            update PostEntity p set p.commentQuantity = p.commentQuantity + 1 where p.postId = :postId
        """)
    void increaseCommentQuantity(Long postId);

    @Query("""
            update PostEntity p set p.commentQuantity = p.commentQuantity - 1 where p.postId = :postId
        """)
    void decreaseCommentQuantity(Long postId);

    @Query("""
            update PostEntity p set p.reactionsQuantity = p.reactionsQuantity - 1 where p.postId = :postId
        """)
    void decreaseReactionsQuantity(Long postId);


}
