package com.GHTK.Social_Network.domain.collection.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
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

  private List<String> images;

  private List<Long> tags;

  private List<Long> reaction;

  private ReactionMessages reactionMsgs;

  private Long reactionQuantity;

  private Instant createAt;
}
