package com.GHTK.Social_Network.infrastructure.payload.dto;

import com.GHTK.Social_Network.domain.model.EChatMessageType;
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

  private EChatMessageType groupType;

  @NotBlank(message = "message cannot blank")
  private String content;

  private EChatMessageType msgType;

  private List<Long> tags = new ArrayList<>();

  private Long reactionQuantity;

  private Long replyMsgId;
}
