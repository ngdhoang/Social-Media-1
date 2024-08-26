package com.GHTK.Social_Network.infrastructure.payload.dto.chat;

import com.GHTK.Social_Network.domain.collection.chat.EGroupType;
import com.GHTK.Social_Network.domain.collection.chat.EMessageType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
  @NotNull(message = "Group id cannot null")
  private String groupId;

  private EGroupType groupType;

  @NotBlank(message = "message cannot blank")
  private String content;

  private EMessageType msgType;

  private List<Long> tags = new ArrayList<>();

  private String replyMsgId;

  private Instant createAt;
}
