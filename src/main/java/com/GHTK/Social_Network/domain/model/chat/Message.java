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

  @AllArgsConstructor
  @Getter
  class ReactionMessages {
    private List<Long> likeIds;
    private List<Long> smileIds;
    private List<Long> argyIds;
    private List<Long> loveIds;
  }

  private Long reactionQuantity;

  private Date createAt;
}
