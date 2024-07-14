package com.GHTK.Social_Network.application.port.input;

public interface ImageHandlerPortInput {
  String uploadImageToCloud(String base64);

  String uploadImagePost(String base64);

  String uploadImageComment(String base64);

  String uploadImageUser(String base64);
}
