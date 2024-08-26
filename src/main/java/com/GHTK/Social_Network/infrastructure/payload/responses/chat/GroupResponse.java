package com.GHTK.Social_Network.infrastructure.payload.responses.chat;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import lombok.Data;

import java.time.Instant;

@Data
public class GroupResponse {
  private String groupId;

  private String groupName;

  private EGroupType groupType;

  private String lastMessage;

  private String groupBackground;

  private boolean isOnline;

  private Instant lastActive;

  private Long messageUnreadCount;

  private boolean inGroup;
}
