package com.GHTK.Social_Network.infrastructure.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetMemBerNickNameRequest {
  private Long userId;

  private String groupId;

  private String nickName;

}
