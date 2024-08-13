package com.GHTK.Social_Network.domain.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Group {
  private String groupId;

  private String groupBackground;

  private String groupName;

  private EGroupType groupType;

  private List<member> members;

  @AllArgsConstructor
  class member {
    private String userId;

    private String nickname;

    private String lastMsgSeen;
  }

  private List<Long> adminIds;

  private List<Long> msgPin;
}
