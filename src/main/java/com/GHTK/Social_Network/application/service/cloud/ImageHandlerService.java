package com.GHTK.Social_Network.application.service.cloud;

import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageHandlerService implements ImageHandlerPortInput {
  private final CloudService cloudService;

  @Override
  public String uploadImageToCloud(String base64) {
    return cloudService.uploadPictureByBase64(base64);
  }

  @Override
  public String uploadImagePost(String base64) {

    return null;
  }

  @Override
  public String uploadImageComment(String base64) {
    return null;
  }

  @Override
  public String uploadImageUser(String base64) {
    return "";
  }
}
