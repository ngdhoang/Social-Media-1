package com.GHTK.Social_Network.infrastructure.repository;

import com.GHTK.Social_Network.infrastructure.entity.post.TagUserEntity;
import com.GHTK.Social_Network.infrastructure.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagUserEntityRepository extends JpaRepository<TagUserEntity, Long> {
  List<TagUserEntity> findAllByUser(UserEntity userEntity);
}
