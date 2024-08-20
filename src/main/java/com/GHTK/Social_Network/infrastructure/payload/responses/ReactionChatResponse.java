package com.GHTK.Social_Network.infrastructure.payload.responses;

import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReactionChatResponse {
  private List<UserBasicDto> user;

  private Long countLike;

  private Long countLove;

  private Long countSmile;

  private Long countAngry;

  private String type;

  public ReactionChatResponse() {
    this.setCountLike(0L);
    this.setCountLove(0L);
    this.setCountSmile(0L);
    this.setCountAngry(0L);
  }
}
