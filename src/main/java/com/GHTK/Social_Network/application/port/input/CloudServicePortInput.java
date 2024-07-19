package com.GHTK.Social_Network.application.port.input;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface CloudServicePortInput {
  Map uploadPictureByBase64(String base64String);

  Map uploadPictureByFile(MultipartFile file);

  Map uploadPictureByFile(MultipartFile file, Long size);

  Map uploadPictureSetSize(String base64String, Long size);

  String extractUrl(Map data);

  String extractPublicId(Map data);

  String extractKey(Map data, String key);

  boolean deletePictureByUrl(String url);
}
