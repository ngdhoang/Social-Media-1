package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
  @Query("""
          select c from CommentEntity c where c.postEntity.postId= ?1
          """)
  List<CommentEntity> findAllByPostId(Long postId);

  @Query("""
          select c from CommentEntity c where c.parentCommentEntity.commentId = ?1    
          """)
  List<CommentEntity> findAllByCommentParentId(Long commentParentId);

  @Query("""
              select c from CommentEntity c where c.postEntity.postId = ?1 and c.parentCommentEntity = null
          """)
  List<CommentEntity> findAllCommentParentIdByPostId(Long postId);

}
