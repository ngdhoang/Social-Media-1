package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.ImagePostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUserEntity;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
  Optional<PostEntity> findByPostIdAndUser(Long postId, UserEntity userEntity);

  @Query("""
          select p from PostEntity p where p.userEntity.userId = ?1
          """)
  List<PostEntity> findAllByUserId(Long userEntityId);

  PostEntity findByTagUsers(TagUserEntity tagUserEntity);

  @Query("""
    select p from PostEntity p where p = ?1
""")
  PostEntity findByImagePostId(Long imagePostEntityId);
}
