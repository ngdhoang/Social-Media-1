package com.GHTK.Social_Network.domain.collection.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message {
  private String msgId;

  private String groupId;

  private Long userAuthId;

  private String replyMsgId;

  private EMessageType msgType;

  private String content;

  private List<String> images;

  private List<Long> tags;

  private List<Long> reaction;

  private ReactionMessages reactionMsgs;

  private Long reactionQuantity;

  private Instant createAt;
}
