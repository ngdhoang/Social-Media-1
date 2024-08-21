package com.GHTK.Social_Network.infrastructure.payload.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {
  private String groupId;

  private String groupName;

  private String groupType;

  private String groupBackground;
}
