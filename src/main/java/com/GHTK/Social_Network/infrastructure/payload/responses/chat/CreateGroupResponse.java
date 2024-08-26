package com.GHTK.Social_Network.infrastructure.payload.responses.chat;

import com.GHTK.Social_Network.infrastructure.payload.dto.chat.GroupDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.chat.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupResponse {
  private GroupDto group;

  private List<MemberDto> members;
}
