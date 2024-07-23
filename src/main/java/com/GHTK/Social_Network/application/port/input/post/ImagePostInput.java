package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.ImagePostDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.post.UpdateImagePostDto;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CreateImageRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ImageResponse;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.List;

public interface ImagePostInput {
  ImagePostDto createImage(CreateImageRequest request) ;

  ImagePostDto updateImage(UpdateImagePostDto imagePostDto);

  MessageResponse deleteImage(Long id);

  @Async
  MessageResponse deleteImageInRedis(String publicId);

  ImageResponse getImageById(Long id);

  List<ImageDto> getImageByPostId(Long id);
}
