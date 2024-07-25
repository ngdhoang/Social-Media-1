package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagUserRepository extends JpaRepository<TagUserEntity, Long> {
  @Query("""
          select t from TagUserEntity t where t.userEntity.userId = ?1
          """)
  List<TagUserEntity> findAllByUserId(Long userId);

  @Query("""
          select t from TagUserEntity t where t.postEntity.postId = ?1
          """)
  List<TagUserEntity> findAllByPostId(Long postId);
}
