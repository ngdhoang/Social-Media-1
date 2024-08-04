package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.CommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
  @Query("""
          select c from CommentEntity c where c.postEntity.postId= ?1 and c.parentCommentEntity is null
          """)
  List<CommentEntity> findAllByPostId(Long postId, Pageable pageable);

  @Query("""
      select c from CommentEntity c where c.postEntity.postId = ?1 and c.userEntity.userId not in ?2
      """)
  List<CommentEntity> findAllByPostIdHandlerBlock(Long postId, List<Long> blockIds);

  @Query("""
          select c from CommentEntity c where c.parentCommentEntity.commentId = ?1    
          """)
  List<CommentEntity> findAllByCommentParentId(Long commentParentId);

  @Query("""
              select c from CommentEntity c where c.postEntity.postId = ?1 and c.parentCommentEntity is null
          """)
  List<CommentEntity> findAllCommentParentIdByPostId(Long postId);

  @Query("""
          select c from CommentEntity c where c.postEntity.postId= ?1
          """)
  List<CommentEntity> getListByPostId(Long postId, List<Long> blockIds, Pageable pageable);

  @Query("""
      select c from CommentEntity c where ( c.postEntity.postId = ?1 and c.userEntity.userId not in ?2)
      """)
  List<CommentEntity> getListByPostIdAndListBlock(Long postId, List<Long> blockIds, Pageable pageable);


  @Query("""
          select c from CommentEntity c where c.userEntity.userId = ?1 
          """)
  List<CommentEntity> getListCommentByUserId(Long userId, Pageable pageable);

  @Query("""
          select c from CommentEntity c where ( c.parentCommentEntity.commentId = ?1  and c.userEntity.userId not in ?2)
          """)
  List<CommentEntity> getListByCommentParentIdAndListBlock(Long commentParentId, List<Long> blockIds, Pageable pageable);

  @Query("""
              select c from CommentEntity c where ( c.postEntity.postId = ?1 and c.parentCommentEntity is null and c.userEntity.userId not in ?2)
          """)
  List<CommentEntity> getListParentIdByPostIdAndListBlock(Long postId, List<Long> blockIds, Pageable pageable);

  @Query("""
            select c from CommentEntity  c where c.userEntity.userId = ?1
          """)
  List<CommentEntity> findAllByInteractions(Long userId);

  @Transactional
  @Modifying
  @Query("UPDATE CommentEntity c SET c.repliesQuantity = c.repliesQuantity + 1 WHERE c.commentId = :commentId")
  void increaseCommentCount(Long commentId);
  @Transactional
  @Modifying
  @Query("""
            update CommentEntity c set c.repliesQuantity = c.repliesQuantity - ?2 where c.commentId = ?1
            """)
  void decreaseCommentCount(Long commentId, Long quantity);

  @Transactional
  @Modifying
  @Query("""
            update CommentEntity c set c.reactionsQuantity = c.reactionsQuantity + 1 where c.commentId = ?1
            """)
  void increaseReactionCount(Long commentId);

  @Transactional
  @Modifying
  @Query("""
            update CommentEntity c set c.reactionsQuantity = c.repliesQuantity - ?2 where c.commentId = ?1
            """)
  void decreaseReactionCount(Long commentId, Long quantity);
}
