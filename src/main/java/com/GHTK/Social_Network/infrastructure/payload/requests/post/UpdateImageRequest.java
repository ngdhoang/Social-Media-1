package com.GHTK.Social_Network.infrastructure.payload.requests.post;

import com.GHTK.Social_Network.infrastructure.payload.dto.post.UpdateImagePostDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateImageRequest {
  private List<UpdateImagePostDto> updateImagePostDtoList;
}
