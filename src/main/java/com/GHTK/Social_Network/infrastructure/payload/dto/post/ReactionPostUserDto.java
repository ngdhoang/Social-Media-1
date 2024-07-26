package com.GHTK.Social_Network.infrastructure.payload.dto.post;

import com.GHTK.Social_Network.domain.model.EReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReactionPostUserDto {

  private Long userId;

  private String lastName;

  private String firstName;

  private String userEmail;

  private String avatar = "";

  private EReactionType type;
}
