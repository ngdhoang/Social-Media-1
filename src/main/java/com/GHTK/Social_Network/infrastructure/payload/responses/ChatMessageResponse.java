package com.GHTK.Social_Network.infrastructure.payload.responses;

import com.GHTK.Social_Network.infrastructure.payload.dto.ChatMessageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {
  private Long userId;

  private ChatMessageDto chatMessage;
}
