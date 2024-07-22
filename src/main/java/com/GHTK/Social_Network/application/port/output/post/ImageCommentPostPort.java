package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.post.comment.ImageComment;

public interface ImageCommentPostPort {
  ImageComment saveImageComment(ImageComment imageComment);

  ImageComment getImageCommentById(Long id);

  void deleteImageCommentById(Long id);
}
