package com.GHTK.Social_Network.infrastructure.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
  private Long userId;

  private String nickname;

  private Long lastMsgSeen;

  private String role;
}
