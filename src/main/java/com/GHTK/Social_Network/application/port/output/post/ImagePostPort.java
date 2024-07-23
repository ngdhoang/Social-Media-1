package com.GHTK.Social_Network.application.port.output.post;

import com.GHTK.Social_Network.domain.model.collection.ImageSequenceDomain;
import com.GHTK.Social_Network.domain.model.post.ImagePost;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Optional;

public interface ImagePostPort {
  ImagePost findImageById(Long id);

  void deleteImageById(Long id);

  @Async
  void deleteImageRedisByPublicId(List<String> publicId, String tail);

  ImagePost saveImagePost(ImagePost imagePost);

  List<ImagePost> saveAllImagePost(List<ImagePost> imagePost);

  ImageSequenceDomain saveImageSequence(ImageSequenceDomain imageSequence);

  Optional<ImageSequenceDomain> findImageSequenceByPostId(Long postId);
}
