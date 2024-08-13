package com.GHTK.Social_Network.infrastructure.payload.responses.post;

import com.GHTK.Social_Network.infrastructure.payload.dto.ImageDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostResponse {
  private Long postId;

  private String content;

  private Date createAt;

  private Date updateAt;

  private String status;

  private List<UserBasicDto> tagUsers;

  private List<ImageDto> imagePosts;

  private Long reactionsQuantity;

  private Long commentQuantity;
}