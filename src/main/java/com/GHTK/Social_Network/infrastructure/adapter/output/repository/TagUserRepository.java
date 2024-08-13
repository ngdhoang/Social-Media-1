package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.TagUserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagUserRepository extends JpaRepository<TagUserEntity, Long> {
  @Query("""
          select t from TagUserEntity t where t.userEntity.userId = ?1 and t.postEntity.postId not in ?2
          """)
  List<TagUserEntity> getListByUserId(Long userId, List<Long> blockIds, Pageable pageable);

  @Query("""
          select t from TagUserEntity t where t.postEntity.postId = ?1 and t.userEntity.userId not in ?2
          """)
  List<TagUserEntity> getListByPostId(Long postId, List<Long> blockIds);

  @Query("""
          select t.userEntity.userId from TagUserEntity t where t.postEntity.postId = ?1 and t.userEntity.userId not in ?2
          """)
  List<Long> getListUserIdByPostId(Long postId, List<Long> blockIds);
}
