package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUser;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagUserRepository extends JpaRepository<TagUser, Long> {
  List<TagUser> findAllByUser(UserEntity userEntity);
}
