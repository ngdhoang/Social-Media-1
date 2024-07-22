package com.GHTK.Social_Network.infrastructure.repository;

import com.GHTK.Social_Network.infrastructure.entity.post.ImagePostEntity;
import com.GHTK.Social_Network.infrastructure.entity.post.PostEntity;
import com.GHTK.Social_Network.infrastructure.entity.post.TagUserEntity;
import com.GHTK.Social_Network.infrastructure.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {
  List<PostEntity> findAllByUserEntity(UserEntity userEntity);

  PostEntity findByTagUserEntities(TagUserEntity tagUserEntity);

  Optional<PostEntity> findByImagePosts(ImagePostEntity imagePost);
}
