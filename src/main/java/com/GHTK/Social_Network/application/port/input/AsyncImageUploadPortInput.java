package com.GHTK.Social_Network.application.port.input;

import org.springframework.web.multipart.MultipartFile;

public interface AsyncImageUploadPortInput {
  void uploadImageAsync (MultipartFile file, String publicId);
}
