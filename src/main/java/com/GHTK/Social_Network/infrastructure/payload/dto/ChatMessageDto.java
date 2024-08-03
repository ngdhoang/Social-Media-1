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
public class ChatMessageDto {
  @NotNull(message = "Chanel id cannot null")
  private Long channelId;

  private EChatMessageType channelType;

  @NotBlank(message = "message cannot blank")
  private String message;

  private String messageType;

  private List<Long> tagUserIds = new ArrayList<>();
}
