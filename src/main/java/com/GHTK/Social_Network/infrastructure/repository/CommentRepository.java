package com.GHTK.Social_Network.infrastructure.repository;

import com.GHTK.Social_Network.infrastructure.entity.postEntity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  @Query("""
              select c from CommentEntity c where c.postEntity.postId= ?1
          """)
  List<Comment> findAllByPostId(Long postId);
}
