package com.GHTK.Social_Network.infrastructure.payload.dto;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.EMessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
  @NotNull(message = "Group id cannot null")
  private Long groupId;

  private EGroupType groupType;

  @NotBlank(message = "message cannot blank")
  private String content;

  private EMessageType msgType;

  private List<Long> tags = new ArrayList<>();

  private Long replyMsgId;
}
