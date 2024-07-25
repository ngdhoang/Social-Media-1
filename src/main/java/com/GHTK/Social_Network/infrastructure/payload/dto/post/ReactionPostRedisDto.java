package com.GHTK.Social_Network.infrastructure.payload.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionPostRedisDto {
  private Long postId;

  private Long userId;

  private List<ReactionPostObjectRedisDto> reactionPostObjectRedisDtos;
}
