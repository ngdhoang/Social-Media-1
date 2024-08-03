package com.GHTK.Social_Network.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
  @NotBlank(message = "Chanel id cannot blank")
  private String channelId;

  private EChatMessageType channelType;

  @NotBlank(message = "message cannot blank")
  private String message;

  private String messageType;

  private List<Long> tagUserIds;
}
