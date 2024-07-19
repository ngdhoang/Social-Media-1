package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.collection.ImageSequence;
import com.GHTK.Social_Network.domain.entity.post.ImagePost;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface ImagePostPort {
  ImagePost findImageById(Long id);

  void deleteImageById(Long id);

  @Async
  void deleteImageRedisByPublicId(List<String> publicId);

  ImagePost saveImagePost(ImagePost imagePost);

  ImageSequence saveImageSequence(ImageSequence imageSequence);

  ImageSequence findImageSequenceByPostId(Long postId);
}
