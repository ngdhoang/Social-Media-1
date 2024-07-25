package com.GHTK.Social_Network.infrastructure.payload.dto.post;

import com.GHTK.Social_Network.domain.model.EReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReactionPostCountDto {

  private Long count;

  private EReactionType reactionType;

}
