package com.GHTK.Social_Network.infrastructure.adapter.output.persistence;

import com.GHTK.Social_Network.application.port.output.post.ImageCommentPostPort;
import com.GHTK.Social_Network.domain.model.ImageComment;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.ImageCommentRepository;
import com.GHTK.Social_Network.infrastructure.mapper.ImageCommentMapperETD;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageCommentPostAdapter implements ImageCommentPostPort {
  private final ImageCommentRepository imageCommentRepository;
  private final ImageCommentMapperETD imageCommentMapperETD;

  @Override
  public ImageComment saveImageComment(ImageComment imageCommentEntity) {
    return imageCommentMapperETD.toDomain(imageCommentRepository.save(
            imageCommentMapperETD.toEntity(imageCommentEntity)
    ));
  }

  @Override
  public ImageComment getImageCommentById(Long id) {
    return imageCommentMapperETD.toDomain(imageCommentRepository.findByCommentIdLong(id));
  }

  @Override
  public void deleteImageCommentById(Long id) {
    imageCommentRepository.deleteById(id);
  }
}
