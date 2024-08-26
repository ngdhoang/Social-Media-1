package com.GHTK.Social_Network.infrastructure.payload.requests.chat.group;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupRequest {
  @NotBlank(message = "Group id cannot blank")
  private String groupId;

  @NotBlank(message = "Group name cannot blank")
  private String groupName;

}
