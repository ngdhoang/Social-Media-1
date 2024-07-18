package com.GHTK.Social_Network.application.port.input;

import org.springframework.web.multipart.MultipartFile;

public interface ImageHandlerPortInput {
  long MAX_SIZE_AVATAR = 512_000; // 500 KB

  long MAX_SIZE_POST = 5_242_880; // 5 MB

  long MAX_SIZE_NOT_VALID = 10_485_760; // 10 MB

  byte[] base64ToByte(String base64);

  boolean isBase64(String base64);

  long base64ImageSizeCalculator(String base64);

  boolean checkSizeValid(String base64, long maxSize);

  boolean isImage(String base64);

  MultipartFile compressImage(String base64, long size);

  MultipartFile convertBase64ToMultipartFile(String base64String);
}
