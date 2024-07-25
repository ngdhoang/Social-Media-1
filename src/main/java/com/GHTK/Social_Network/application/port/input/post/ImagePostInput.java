package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.domain.model.post.ImagePost;
import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ImagePostDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.UpdateImagePostDto;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ImageResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ImagePostInput {
  String POST_TAIL = "POST";
  String COMMENT_TAIL = "COMMENT";
  String VALUE_LOADING = "LOADING";
  int MAX_LENGTH_GENERATE = 8;

  ImagePostDto createImage(MultipartFile image, String tail) ;

  @Async
  void deleteImagePost(List<ImagePost> imagePost);
}
