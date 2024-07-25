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
              SELECT p FROM PostEntity p
              WHERE p.userEntity.userId = :userId
              AND (
                  p.postStatus = :status
                  OR (:status = 'PRIVATE' AND p.postStatus IN (com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.PRIVATE))
                  OR (:status = 'PUBLIC' AND p.postStatus IN (com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.PUBLIC))
                  OR (:status = 'FRIEND' AND p.postStatus IN (com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.FRIEND, com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.PUBLIC))
                  OR (:status = 'ALL' AND p.postStatus IN (com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.FRIEND, com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.PUBLIC, com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.EPostStatusEntity.PRIVATE))
              )
          """)
  List<PostEntity> findAllByUserIdAndFriendStatus(@Param("userId") Long userId, @Param("status") String status);
}
