package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.EStateUserGroupCollection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCollection {
  private Long userId;
  private String nickname;

  private String lastMsgSeen;

  private Instant lastTimeMsgSeen;

  private EStateUserGroupCollection role;
}
