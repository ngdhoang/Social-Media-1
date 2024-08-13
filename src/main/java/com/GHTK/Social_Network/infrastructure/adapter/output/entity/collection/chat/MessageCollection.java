package com.GHTK.Social_Network.infrastructure.adapter.output.entity.collection.chat;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageCollection {
  @Id
  private String msgId;

  private String groupId;

  private Long userAuthId;

  private Long replyMsgId;

  private EMessageTypeCollection msgType;

  private String content;

  private List<Long> tags;

  private ReactionMessagesCollection reactionMsgs;

  private Long reactionQuantity;

  @CreatedDate
  private Instant createAt;
}
