package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.post.ImageCommentPostPort;
import com.GHTK.Social_Network.domain.entity.post.comment.ImageComment;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ImageCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class imageCommentPostPortImpl implements ImageCommentPostPort {
  private final ImageCommentRepository imageCommentRepository;

  @Override
  public ImageComment saveImageComment(ImageComment imageComment) {
    return imageCommentRepository.save(imageComment);
  }

  @Override
  public ImageComment getImageCommentById(Long id) {
    return imageCommentRepository.findById(id).orElse(null);
  }

  @Override
  public void deleteImageCommentById(Long id) {
    imageCommentRepository.deleteById(id);
  }
}
