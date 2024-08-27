package com.GHTK.Social_Network.infrastructure.payload.requests.chat.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequest {
  @NotBlank(message = "Group id cannot blank")
  private String groupId;

  @Size(message = "Group members must be at least 1", min = 1)
  private List<Long> memberId;
}
