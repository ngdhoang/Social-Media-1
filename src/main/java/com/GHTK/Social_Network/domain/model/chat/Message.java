package com.GHTK.Social_Network.domain.model.chat;

import lombok.*;

import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message {
  private String msgId;

  private String groupId;

  private Long userAuthId;

  private Long replyMsgId;

  private EMessageType msgType;

  private String content;

  private List<Long> tags;

  private List<Long> reaction;

  private ReactionMessages reactionMsgs;

  private Long reactionQuantity;

  private Date createAt;
}
