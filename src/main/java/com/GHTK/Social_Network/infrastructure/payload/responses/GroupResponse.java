package com.GHTK.Social_Network.infrastructure.payload.responses;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.infrastructure.payload.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse {
  private String groupId;

  private String groupName;

  private EGroupType groupType;

  private String groupBackground;

  private List<MemberDto> members;

  private List<Long> msgPin;

  private LocalDate createAt;
}
