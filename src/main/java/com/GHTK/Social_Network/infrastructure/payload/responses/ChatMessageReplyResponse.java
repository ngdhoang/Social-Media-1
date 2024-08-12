package com.GHTK.Social_Network.infrastructure.payload.responses;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChatMessageReplyResponse {
  private ChatMessageResponse msgQuote;

  private ChatMessageResponse msgReply;
}
