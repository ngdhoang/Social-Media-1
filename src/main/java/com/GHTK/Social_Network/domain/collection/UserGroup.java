package com.GHTK.Social_Network.domain.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroup {
  private String groupId;

  private String lastMsgId;

  private String lastGroupName;

  private EStateUserGroup state;

  public UserGroup(String groupId, EStateUserGroup state) {
    this.groupId = groupId;
    this.state = state;
  }
}
