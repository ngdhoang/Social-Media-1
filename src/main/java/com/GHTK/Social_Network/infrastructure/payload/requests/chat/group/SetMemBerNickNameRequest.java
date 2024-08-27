package com.GHTK.Social_Network.infrastructure.payload.requests.chat.group;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetMemBerNickNameRequest {
//  private Long userId;

  @NotBlank
  private String groupId;

  @NotBlank
  private String nickName;

}
