package com.GHTK.Social_Network.infrastructure.payload.responses.chat;

import com.GHTK.Social_Network.infrastructure.payload.dto.user.UserBasicDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListChatMessageResponse {
  private List<ChatMessageReplyResponse> message;

  private List<UserBasicDto> userSeen;

  private int countUserSeen;
}
