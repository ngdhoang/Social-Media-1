package com.GHTK.Social_Network.infrastructure.payload.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {
  private Long id;

  private String url;
}
