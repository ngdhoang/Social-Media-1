package com.GHTK.Social_Network.application.port.output;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudPort {
  Map uploadPictureByFile(MultipartFile file);

  Map uploadPictureByFile(MultipartFile file, Long size);

  String extractUrl(Map data);

  String extractPublicId(Map data);

  String extractByKey(Map data, String key);

  boolean deletePictureByUrl(String url);
}
