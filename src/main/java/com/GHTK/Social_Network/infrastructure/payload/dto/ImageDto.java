package com.GHTK.Social_Network.infrastructure.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {
  private Long id;

  private String imageUrl;

  private Date createAt;

  private Long postId;
}
