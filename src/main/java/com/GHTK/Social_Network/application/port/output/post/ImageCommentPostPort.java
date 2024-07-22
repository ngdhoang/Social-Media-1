package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.infrastructure.entity.post.comment.ImageCommentEntity;

public interface ImageCommentPostPort {
  ImageCommentEntity saveImageComment(ImageCommentEntity imageComment);

  ImageCommentEntity getImageCommentById(Long id);

  void deleteImageCommentById(Long id);
}
