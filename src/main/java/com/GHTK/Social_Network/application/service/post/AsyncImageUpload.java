package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.CloudServicePortInput;
import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.service.cloud.CustomMultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AsyncImageUpload {
  private final CloudServicePortInput cloudServicePortInput;

  private final RedisTemplate<String, String> imageRedisTemplate;

  @Async
  public CompletableFuture<String> uploadImageAsync(MultipartFile file, String publicId) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        byte[] fileData = file.getBytes();
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();

        CustomMultipartFile customFile = new CustomMultipartFile(fileData, "file", originalFilename, contentType);

        Map<String, Object> uploadResult = cloudServicePortInput.uploadPictureByFile(customFile, ImageHandlerPortInput.MAX_SIZE_POST);
        String imageUrl = cloudServicePortInput.extractUrl(uploadResult);

        imageRedisTemplate.opsForValue().set(publicId, imageUrl);

        return imageUrl;
      } catch (IOException e) {
        throw new RuntimeException("Failed to process image upload", e);
      }
    });
  }
}
