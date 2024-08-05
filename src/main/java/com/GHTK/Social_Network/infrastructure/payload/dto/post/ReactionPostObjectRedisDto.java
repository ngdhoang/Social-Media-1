package com.GHTK.Social_Network.infrastructure.payload.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReactionPostObjectRedisDto {
  Long postId;

  Long userId;

  Long reactionPostId;

  LocalDate createAt;
}
