package com.GHTK.Social_Network.infrastructure.payload.dto.post;

import com.GHTK.Social_Network.domain.model.post.EReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReactionCountDto {
  private Long quantity;

  private EReactionType type;
}
