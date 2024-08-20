package com.GHTK.Social_Network.infrastructure.payload.requests;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupRequest {
  private String groupName;

  private EGroupType groupType;

  private List<Long> members;
}
