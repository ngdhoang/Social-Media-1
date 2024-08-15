package com.GHTK.Social_Network.infrastructure.payload.responses;

import com.GHTK.Social_Network.infrastructure.payload.dto.MessageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {
  private Long userId;

  private Long msgId;

  private MessageDto message;

  private Long reactionQuantity;

  private List<String> images;
}
