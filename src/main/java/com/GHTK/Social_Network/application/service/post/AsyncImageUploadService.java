package com.GHTK.Social_Network.application.service.post;

import com.GHTK.Social_Network.application.port.input.AsyncImageUploadPortInput;
import com.GHTK.Social_Network.application.port.input.ImageHandlerPortInput;
import com.GHTK.Social_Network.application.port.output.CloudPort;
import com.GHTK.Social_Network.application.port.output.post.RedisImageTemplatePort;
import com.GHTK.Social_Network.domain.CustomMultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AsyncImageUploadService implements AsyncImageUploadPortInput {
  private final RedisImageTemplatePort redisImageTemplatePort;
  private final CloudPort cloudPort;

  @Async
  @Override
  public CompletableFuture<String> uploadImageAsync(MultipartFile file, String publicId) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        byte[] fileData = file.getBytes();
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();

        CustomMultipartFile customFile = new CustomMultipartFile(fileData, "file", originalFilename, contentType);

        Map<String, Object> uploadResult = cloudPort.uploadPictureByFile(customFile, ImageHandlerPortInput.MAX_SIZE_POST);
        String imageUrl = cloudPort.extractUrl(uploadResult);
        redisImageTemplatePort.createOrUpdate(publicId, imageUrl);
        return imageUrl;
      } catch (IOException e) {
        throw new RuntimeException("Failed to process image upload", e);
      }
    });
  }
}
