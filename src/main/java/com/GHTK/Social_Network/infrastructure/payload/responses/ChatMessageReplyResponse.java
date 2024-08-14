package com.GHTK.Social_Network.infrastructure.payload.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageReplyResponse {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ChatMessageResponse msgQuote;

  private ChatMessageResponse msgReply;
}
