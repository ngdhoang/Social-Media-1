package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ImagePostDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImagePostInput {
  String POST_TAIL = "_POST_";
  String COMMENT_TAIL = "_COMMENT_";
  String VALUE_LOADING = "_LOADING_";
  int MAX_LENGTH_GENERATE = 8;

  ImagePostDto createImage(MultipartFile image, String tail);

  @Async
  void deleteImagePost(List<ImagePost> imagePost);
}
