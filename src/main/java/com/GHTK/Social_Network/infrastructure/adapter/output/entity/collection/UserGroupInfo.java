package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupInfo {
  private String groupId;

  private String lastGroupName;

  private String lastMsgId;

  private Instant lastMsgTime;
}
