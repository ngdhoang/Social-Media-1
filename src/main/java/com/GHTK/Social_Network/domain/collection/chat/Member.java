package com.GHTK.Social_Network.domain.collection.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {
  private Long userId;

  private String nickname;

  private String lastMsgSeen;

  private EMemberRole role;
}
