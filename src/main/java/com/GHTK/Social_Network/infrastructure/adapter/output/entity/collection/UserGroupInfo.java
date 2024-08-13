package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupInfo {
  private String groupId;

  private String lastMsgId;

  private EStateUserGroupCollection state;
}
