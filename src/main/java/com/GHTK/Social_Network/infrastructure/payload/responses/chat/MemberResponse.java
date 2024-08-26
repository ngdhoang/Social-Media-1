package com.GHTK.Social_Network.infrastructure.payload.responses.chat;

import com.GHTK.Social_Network.infrastructure.payload.dto.chat.MemberDto;
import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
  private MemberDto member;

  private UserBasicDto user;
}
