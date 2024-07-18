package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.infrastructure.payload.dto.post.ImageDto;
import lombok.Data;

import java.util.List;

@Data
public class ImageResponse {
  private List<ImageDto> imageDtoList;
}
