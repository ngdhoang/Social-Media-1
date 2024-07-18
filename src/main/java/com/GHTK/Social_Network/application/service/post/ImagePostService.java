package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.post.ImagePostInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.CreateImageRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.MessageResponse;
import com.GHTK.Social_Network.infrastructure.payload.responses.post.ImageResponse;

public class ImagePostService implements ImagePostInput {
  @Override
  public ImageResponse createImage(CreateImageRequest request) {

    return null;
  }

  @Override
  public ImageResponse updateImage(PostRequest postRequest) {
    return null;
  }

  @Override
  public MessageResponse deleteImage(Long id) {
    return null;
  }

  @Override
  public ImageResponse getImageById(Long id) {
    return null;
  }

  @Override
  public ImageResponse getImageByPost(String url) {
    return null;
  }
}
