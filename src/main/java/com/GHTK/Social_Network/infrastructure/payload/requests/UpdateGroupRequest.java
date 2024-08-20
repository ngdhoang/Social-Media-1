package com.GHTK.Social_Network.infrastructure.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupRequest {
  private String groupId;

  private String groupName;

  private List<Long> members;
}
