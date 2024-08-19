package com.GHTK.Social_Network.domain.collection.chat;

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
  private String id;

  private String groupBackground;

  private String groupName;

  private EGroupType groupType;

  private List<Member> members;

  private List<String> msgPin;

}
