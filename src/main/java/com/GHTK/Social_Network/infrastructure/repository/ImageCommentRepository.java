package com.GHTK.Social_Network.infrastructure.repository;

import com.GHTK.Social_Network.infrastructure.entity.post.comment.ImageCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageCommentRepository extends JpaRepository<ImageCommentEntity, Long> {
}
