package com.GHTK.Social_Network.application.port.input.post;

import com.GHTK.Social_Network.infrastructure.payload.requests.post.CreateImageRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ImageResponse;

public interface ImagePostInput {
  ImageResponse createImage(CreateImageRequest request);

  ImageResponse updateImage(PostRequest postRequest);

  MessageResponse deleteImage(Long id);

  ImageResponse getImageById(Long id);

  ImageResponse getImageByPost(String url);
}
