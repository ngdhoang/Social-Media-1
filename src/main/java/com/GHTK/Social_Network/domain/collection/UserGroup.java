package com.GHTK.Social_Network.domain.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGroup {
  private String groupId;

  private String lastMsgId;

  private String lastGroupName;
}
