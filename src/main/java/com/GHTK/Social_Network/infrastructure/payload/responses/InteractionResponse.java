package com.GHTK.Social_Network.infrastructure.payload.responses;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InteractionResponse {
  private Long roleId;

  private String role;

  private UserBasicDto owner;

  private EReactionType reactionType;

  private String content;

  private String image;

  private LocalDate createAt;

  private LocalDate updateAt;
}
