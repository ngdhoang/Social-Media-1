package com.GHTK.Social_Network.infrastructure.payload.requests.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class CreateImageRequest {
  private MultipartFile file;
}
