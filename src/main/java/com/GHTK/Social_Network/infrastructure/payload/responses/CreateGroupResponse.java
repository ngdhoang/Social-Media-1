package com.GHTK.Social_Network.infrastructure.payload.responses;

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
public class CreateGroupResponse {
  private String groupId;

  private String groupName;

  private String groupType;

  private String groupBackground;

  private List<MemberDto> members;

  private List<String> msgPin;

  private LocalDate createAt;
}
