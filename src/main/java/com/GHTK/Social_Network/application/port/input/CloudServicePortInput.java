package com.GHTK.Social_Network.application.port.input;

import org.springframework.web.multipart.MultipartFile;

public interface CloudServicePortInput {
  String uploadPictureByBase64(String base64String);

  String uploadPictureByFile(MultipartFile file);

  String uploadPictureSetSize(String base64String, Long size);

  boolean deletePictureByUrl(String url);
}
