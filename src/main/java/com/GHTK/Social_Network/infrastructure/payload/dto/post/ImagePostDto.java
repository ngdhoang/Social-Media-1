package com.GHTK.Social_Network.infrastructure.payload.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagePostDto {
  private String PublicId;

  private String url;

  public ImagePostDto(String url) {
    this.url = url;
  }
}
