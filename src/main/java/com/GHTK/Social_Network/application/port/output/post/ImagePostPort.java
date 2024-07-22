package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.infrastructure.entity.collection.ImageSequence;
import com.GHTK.Social_Network.infrastructure.entity.post.ImagePostEntity;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface ImagePostPort {
  ImagePostEntity findImageById(Long id);

  void deleteImageById(Long id);

  @Async
  void deleteImageRedisByPublicId(List<String> publicId);

  ImagePostEntity saveImagePost(ImagePostEntity imagePost);

  ImageSequence saveImageSequence(ImageSequence imageSequence);

  ImageSequence findImageSequenceByPostId(Long postId);
}
