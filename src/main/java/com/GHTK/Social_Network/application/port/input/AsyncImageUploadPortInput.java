package com.GHTK.Social_Network.application.port.input;

import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface AsyncImageUploadPortInput {
  CompletableFuture<String> uploadImageAsync (MultipartFile file, String publicId);
}
