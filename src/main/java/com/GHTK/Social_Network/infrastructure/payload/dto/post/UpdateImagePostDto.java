package com.GHTK.Social_Network.infrastructure.payload.dto.post;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateImagePostDto {
  private Long imageId;

  private MultipartFile image;
}
