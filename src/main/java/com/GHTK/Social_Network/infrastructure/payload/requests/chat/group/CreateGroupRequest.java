package com.GHTK.Social_Network.infrastructure.payload.requests.chat.group;

import com.GHTK.Social_Network.common.customAnnotation.config.ValidPattern;
import com.GHTK.Social_Network.common.customAnnotation.logic.CustomPatternValidator;
import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupRequest {
  @NotBlank(message = "Group name cannot blank")
  private String groupName;

  @ValidPattern(CustomPatternValidator.GROUP_TYPE)
  private String groupType;

  @Size(message = "Group members must be at least 2", min = 2)
  private List<Long> members;
}
