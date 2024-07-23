package com.GHTK.Social_Network.application.port.output.post;


import com.GHTK.Social_Network.domain.model.ImageComment;

public interface ImageCommentPostPort {
  ImageComment saveImageComment(ImageComment imageCommentEntity);

  ImageComment getImageCommentById(Long id);

  void deleteImageCommentById(Long id);
}
